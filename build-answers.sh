#!/bin/bash

set -eo pipefail

proj_dir=~/dev/wordle/
cd "$(mktemp -d)"

# Eliminate plurals
grep '^....$' /usr/share/dict/words | \
grep -v chao | \
grep -v gros | \
grep -v toru | \
sed -e 's/$/s/' | sort > plurals

comm -2 -3 $proj_dir/src/main/resources/wordlist plurals > sans-plurals

# Eliminate "ed" suffix
grep '^...$' /usr/share/dict/words > 3letters
grep -v bus < 3letters | \
grep -v shi | \
grep -v tri | \
sed -e 's/$/ed/' | sort > eds

comm -2 -3 sans-plurals eds
