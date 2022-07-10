# Wordle Guesser

A Wordle guessing interactive command line application. Features include:

* An interactive mode allowing selection of top guesses from a menu or entry of any arbitrary word (that is in the
  master list) as a guess.
* An "auto" mode where the sequence of guesses it would take to solve a provided word are printed.

## Installation

### Requirements

* Java 15 or later (download a JRE from https://adoptium.net/ if you don't know what this means).
* In addition to the downloaded zip, it will need around 90MB of disk space to store precalculated results.

### Steps

1. Install java if not already present
2. Download the latest release zip and extract.
3. Run the `result-pre-calc-app` (with .bat extension if on windows) from the bin folder. This will take a while to run
   depending on your computer and will generate a file called  `results.lz4`, which the main app uses.

## Example

### A couple of automatic solving sessions

```commandline
12972 words read
Precalculated results read
10051 answers read
> as entry
10051 possible words remaining
selecting guess: "soare"
result:           bbbgy
44 possible words remaining
selecting guess: "teend"*
result:           yybyb
1 possible words remaining
selecting guess: "entry"
result:           ggggg
answer "entry" found in 3 guesses
> as guess
10051 possible words remaining
selecting guess: "soare"
result:           ybbby
279 possible words remaining
selecting guess: "tenes"
result:           bybbg
26 possible words remaining
selecting guess: "gleis"
result:           gbgbg
1 possible words remaining
selecting guess: "guess"
result:           ggggg
answer "guess" found in 4 guesses
> q
```

### An interactive solving session

```commandline
12972 words read
Precalculated results read
10051 answers read
> int
10051 possible words remaining
10 best words:
0.	soare	6.101473141272062
1.	lares	6.054817012136371
2.	raise	6.042831023390049
3.	reais	6.031265175244809
4.	tares*	6.029768300024893
5.	rales	6.016141644011224
6.	saine	6.0059888022275265
7.	serai	5.994384408685323
8.	nares	5.993677242178345
9.	saner	5.97912913070767
Enter number from menu or type guess word
>int>select-guess> flood
Result for flood?
>int>ask-result> bbbbb
3894 possible words remaining
10 best words:
0.	tares*	6.504827152754846
1.	raise	6.451267831254869
2.	saine	6.424686856821378
3.	reais	6.4067657886975455
4.	nares	6.396527518648409
5.	teras*	6.394800987664387
6.	tears*	6.394784976799859
7.	rates*	6.383132426207532
8.	strae	6.373624859450402
9.	saner	6.368701770148231
Enter number from menu or type guess word
>int>select-guess> 0
Result for tares?
>int>ask-result> bbbyb
166 possible words remaining
10 best words:
0.	genie	5.47901607317045
1.	penie	5.410297213828691
2.	mince	5.344356032194411
3.	hinge	5.3423948795738285
4.	eigne	5.322988087800146
5.	henge	5.316738291032021
6.	binge	5.274679166726918
7.	menge	5.273966640121784
8.	minge	5.2568594127020685
9.	niece	5.233112554208957
Enter number from menu or type guess word
>int>select-guess> 1
Result for penie?
>int>ask-result> bbgyg
12 possible words remaining
10 best words:
0.	gawcy*	2.792481250360578
1.	gawky*	2.6887218755408675
2.	gamic*	2.6258145836939115
3.	gowks*	2.459147917027245
4.	gawks*	2.459147917027245
5.	gizmo*	2.459147917027245
6.	zymic*	2.4508257945180887
7.	whack*	2.4182958340544896
8.	chowk*	2.4182958340544896
9.	gucky*	2.4182958340544896
Enter number from menu or type guess word
>int>select-guess> 0
Result for gawcy?
>int>ask-result> ybbbb
3 possible words remaining
10 best words:
0.	sahib*	1.5849625007211563
1.	whams*	1.5849625007211563
2.	chibs*	1.5849625007211563
3.	belah*	1.5849625007211563
4.	ihram*	1.5849625007211563
5.	bight*	1.5849625007211563
6.	milch*	1.5849625007211563
7.	miche*	1.5849625007211563
8.	machi*	1.5849625007211563
9.	mashy*	1.5849625007211563
Enter number from menu or type guess word
>int>select-guess> 0
Result for sahib?
>int>ask-result> bbyyb
1 possible words remaining
1 best words:
0.	hinge	1.0
Enter number from menu or type guess word
>int>select-guess> 0
Result for hinge?
>int>ask-result> ggggg
> q
```

## Usage

* Run `interactive-app`. This will load some data and then give you a prompt. There's no built-in help yet, my bad.

### Main menu ("> ") commands

* "quit" ("q"): I'll give you one guess what this does.
* "interactive-solve" ("int"): Starts an interactive solving session.
* "auto-solve \<word>" ("as \<word>"): Runs through the solving session for \<word> non-interactively, printing out
  various information along the way.
* "answer-list \<filename>" ("al \<filename>"): Loads a list of possible answers from \<filename> (or reverts to the
  built-in list if filename is not specified). The file must contain one word per line. Possible guesses still come from
  the built-in master list. This list of words must be a subset of the [master list](src/main/resources/wordlist).

### Guess selection menu (">int>select-guess> "):

* Enter a number to select a guess from the menu.
* Enter a valid 5 letter word from the master list to use that word as the guess.
* "q" to abort the solving session and return to the main menu.

### Result entry (">int>ask-result> ")

* Enter 5 letters ('b'lack, 'y'ellow, 'g'reen); one for each position. eg. `bbybg` for the 3rd letter being in the word
  but in the wrong position and the 5th letter being correct for that position.
    * `ggggg` will return to the main menu
* "q" to abort the solving session and return to the main menu.

## Known issues/limitations

There's lots of rough edges at the moment.

* In a few places it's not too hard to cause a crash.
* Invalid input generates confusing error messages and no useful help.
* Lots of generally hardcoded, non-configurable behavior.

## Future ideas

* Supporting word weights. Currently every possible answer is considered equally likely, which leads to some interesting
  suggestions towards the end of a solve.
* Supporting "hard mode" (every guess must be consistent with past results). The solver is pretty greedy in that it only
  considers which guesses are likely to eliminate the most words, and not to avoid the dreaded "just need to work out
  the last letter" that is the bane of playing in hard mode.
* Batch app. Currently can be fudged by piping something like

```
as rebus
as boost
as truss
as siege
q
```

to the standard input and then using grep/sed/cut over the output, but a proper batch mode would be nice.

* Support for some of the various "multi-word" variants of wordle that exist.
* Support for dumping out various info/stats during interactive mode, eg.
    * The list of still possible answers
    * The score of a specific guess
    * The result distribution for a specific guess
* Better configurability, eg. number of menu guesses to offer
* Support for an answer tree instead of greedily choosing the best guess each time

## Performance

The default configuration uses all 12972 words that wordle will accept as the master guess list. The default answer list
is the master guess with a heap of plural words and words ending in "ed" stripped out, currently a bit over 10000 words.

In the default configuration it seems to average a bit under 4 guesses. Refining the answer list could reduce this, but
that hasn't been my focus. By "cheating" and loading the actual answer list that wordle uses (whether this is cheating
or just overfitting is a point that can be debated), the average drops to 3.43 guesses, which is close to the best
possible
average that can be achieved in wordle as it currently exists if you forget all the previous
answers that have appeared.

## Other links

https://mottaquikarim.github.io/wordle_timemachine/ - A great site for playing past (or future) wordle games.

https://www.youtube.com/watch?v=v68zYyaEmEA - A great video on the maths for solving wordle. The current version of the
solver uses a lot of the maths described in this video.

https://www.youtube.com/watch?v=sVCe779YC6A - Another video describing some different solving approaches.