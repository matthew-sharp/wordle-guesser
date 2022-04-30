#!/bin/bash

grep -E '^[a-z]{5}$' /usr/share/dict/words | grep -Fvf blacklist  > wordlist
