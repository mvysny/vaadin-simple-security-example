#!/usr/bin/env bash
# Checks the doc layer — the files described in AGENTS.md, "Design docs" — and nothing else:
#   - every D_ / R_ slug cited outside design/ideas/ has its "## " heading in
#     design/decisions.md / design/research.md
#   - every "## D_" heading reads "D_<slug> — <the question>?"; decisions.md carries no
#     "Superseded by" / "Amended by" / strikethrough / line-initial "Status:"
#   - root AGENTS.md is under 34 KB — the one size rule: it is loaded every turn
#   - every nested AGENTS.md is named in the root AGENTS.md module map
#   - every CLAUDE.md is a symlink to AGENTS.md, and one sits beside every AGENTS.md
#   - every line of the root AGENTS.md "What this is" section occurs verbatim in README.md
#   - no retired doc files: root UPPERCASE docs, design/requirements.md, design/solution*.md, …
# Run from anywhere inside the repo; needs bash and git. Exit 0 when green, 1 with one
# "tripwire:" line per failure otherwise. A failure is as often a stale expectation as a real
# violation — read the line before "fixing" the doc.
#
# The set of checks is closed: a project never adds one here. Rules about the project's own code
# belong in its tests and linters, which are better at it. The checks carry no ids — each is
# named by what it checks.
set -euo pipefail
export LC_ALL=C   # byte order for sort/comm, byte count for the cap
cd "$(git rev-parse --show-toplevel)"

fail=0
err() { printf 'tripwire: %s\n' "$*" >&2; fail=1; }
DESIGN=design

# Tracked files, listed once. An empty listing is a failure: a vacuous pass is indistinguishable
# from a green one.
files=()
while IFS= read -r -d '' f; do files+=("$f"); done < <(git ls-files -z)
[ "${#files[@]}" -gt 0 ] || { err "git ls-files listed nothing in $PWD"; exit 1; }
tracked() { printf '%s\0' "${files[@]}"; }

# --- cited slug → heading -----------------------------------------------------
# $1 = prefix letter, $2 = the file whose "## <prefix>_<slug>" headings define them.
check_namespace() {
  local prefix=$1 file=$2 pattern cited defined missing slug
  pattern="\\b${prefix}_[a-z][a-z0-9_]*"
  # An idea names the entries it will add once it graduates, so ideas cite nothing yet.
  cited=$(tracked | grep -z -v "^$DESIGN/ideas/" | xargs -0 grep -I -o -h -E "$pattern" 2>/dev/null | sort -u || true)
  if [ ! -f "$file" ]; then
    [ -z "$cited" ] || err "$file does not exist but ${prefix}_ slugs are cited: $(printf '%s ' $cited)"
    return
  fi
  defined=$(grep -o -E "^## ${prefix}_[a-z][a-z0-9_]*" "$file" | sed 's/^## //' | sort -u || true)
  missing=$(comm -23 <(printf '%s\n' "$cited") <(printf '%s\n' "$defined") | sed '/^$/d' || true)
  for slug in $missing; do
    err "\`$slug\` is cited but has no '^## $slug' heading in $file"
  done
}
check_namespace D "$DESIGN/decisions.md"
check_namespace R "$DESIGN/research.md"

# --- decisions.md: every heading is a question; answers are rewritten, never amended ---------
if [ -f "$DESIGN/decisions.md" ]; then
  while IFS= read -r line; do
    printf '%s\n' "$line" | grep -q -E '^## D_[a-z][a-z0-9_]* — .*\?$' \
      || err "$DESIGN/decisions.md: '$line' — a heading is '## D_<slug> — <the question>?', as in '## D_<slug> — Why <this> rather than <that>?'"
  done < <(grep -E '^## D_' "$DESIGN/decisions.md" || true)
  while IFS= read -r hit; do
    err "$DESIGN/decisions.md:$hit — an answer is rewritten in place; a reversal is a rewrite or a why-not question, never an amendment"
  done < <(grep -n -E 'Superseded by|Amended by|~~|^(\*\*)?Status:' "$DESIGN/decisions.md" || true)
fi

# --- the root AGENTS.md is capped; nested files are linked from the root map ----
ROOT_LIMIT=$((34 * 1024))
if [ -f AGENTS.md ]; then
  size=$(wc -c < AGENTS.md)
  [ "$size" -le "$ROOT_LIMIT" ] || err "AGENTS.md is $size bytes; the cap is $ROOT_LIMIT — see 'Maintenance of this file' in AGENTS.md"
else
  err "no root AGENTS.md"
fi
while IFS= read -r -d '' f; do
  case "$f" in
    */AGENTS.md)
      if [ -f AGENTS.md ] && ! grep -q -F -- "$f" AGENTS.md; then
        err "$f is not named in the root AGENTS.md module map — add 'Rules: $f' to its module's line"
      fi ;;
  esac
done < <(tracked)

# --- CLAUDE.md is a symlink to AGENTS.md, both directions ----------------------
while IFS= read -r -d '' f; do
  case "$f" in
    CLAUDE.md|*/CLAUDE.md)
      if ! [ -L "$f" ] || [ "$(readlink "$f")" != "AGENTS.md" ]; then
        err "$f must be a symlink to AGENTS.md (ln -s AGENTS.md CLAUDE.md); any content moves into AGENTS.md"
      fi ;;
    AGENTS.md|*/AGENTS.md)
      link="$(dirname "$f")/CLAUDE.md"
      [ -e "$link" ] || err "$link is missing — ln -s AGENTS.md CLAUDE.md beside $f" ;;
  esac
done < <(tracked)

# --- the pitch is the one sanctioned duplicate: hold it verbatim -----------------
if [ -f AGENTS.md ] && [ -f README.md ]; then
  body=$(awk '/^## What this is/{f=1; next} /^## /{f=0} f' AGENTS.md | sed '/^[[:space:]]*$/d')
  if [ -z "$body" ]; then
    err "AGENTS.md has no '## What this is' section — the README's opening, copied verbatim"
  else
    while IFS= read -r line; do
      grep -q -x -F -- "$line" README.md \
        || err "AGENTS.md 'What this is' must be a verbatim copy of README.md lines; not found there: $line"
    done <<< "$body"
  fi
fi

# --- retired files ----------------------------------------------------------------
for f in DECISIONS.md decisions.md NOTES.md RESEARCH.md SOLUTION.md SOLUTION_VERIFY.md \
         REQUIREMENTS.md ARCHITECTURE.md DESIGN.md COMPARISON.md; do
  if [ -e "$f" ]; then err "root $f — doc files live under $DESIGN/, lowercase"; fi
done
if [ -e "$DESIGN/requirements.md" ]; then err "$DESIGN/requirements.md is retired — promises are AGENTS.md lines, owner-written"; fi
if [ -e "$DESIGN/comparison.md" ]; then err "$DESIGN/comparison.md is retired — prior art is research.md"; fi
for f in "$DESIGN"/solution.md "$DESIGN"/solution-*.md; do
  if [ -e "$f" ]; then err "$f is retired — one normative $DESIGN/architecture.md, deliverables as sections"; fi
done

if [ "$fail" -eq 0 ]; then echo "design tripwires: ok"; fi
exit "$fail"
