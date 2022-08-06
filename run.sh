#!/bin/bash

srcdir=/home/matt/dev/wordle
jar=$srcdir/core/target/scala-3.1.3/core-assembly-1.1.0.jar
answer_file=wordle-answer-list.txt

tmpdir=$(mktemp -d)

#echo tmpdir is $tmpdir
trap 'rm -r "$tmpdir"' EXIT

cp $answer_file "$tmpdir"
ln -s $srcdir/results.lz4 "$tmpdir/results.lz4"

cd "$tmpdir" || exit

while [ "$(wc -l < $answer_file)" -gt 0 ]; do
  word=$(head -n 1 $answer_file)
  echo $word | java -jar $jar -al $answer_file -s -
  tail -n +2 $answer_file > ${answer_file}.new
  mv ${answer_file}.new $answer_file
done