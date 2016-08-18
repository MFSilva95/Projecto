/*************************************************************************
*									 *
*	 YAP Prolog 							 *
*									 *
*	Yap Prolog was developed at NCCUP - Universidade do Porto	 *
*									 *
* Copyright L.Damas, V.S.Costa and Universidade do Porto 1985-1997	 *
*									 *
**************************************************************************
*									 *
* File:		random.yap						 *
* Last rev:	5/12/99							 *
* mods:									 *
* comments:	Random operations					 *
*									 *
*************************************************************************/

/**
 * @file   random.yap
 * @author original code from RA O'Keefe.
 * @author VITOR SANTOS COSTA <vsc@VITORs-MBP.lan>
 * @date   Wed Nov 18 00:05:21 2015
 *
 * @brief Integer Random Number Generator
 *
 *
*/

:- module(random, [
	random/1,
	random/3,
	randseq/3,
	randset/3,
	getrand/1,
	setrand/1
    ]).

/** @defgroup random Random Number Generator
@ingroup library
@{

 Since YAP-4.3.19 YAP uses
the O'Keefe public-domain algorithm, based on the "Applied Statistics"
algorithm AS183.

The following random number operations are included with the
`use_module(library(random))` command.

In ROK's words: ``This is algorithm AS 183 from Applied Statistics.  I also have a C
   version.  It is really very good.  It is straightforward to make a
   version which yields 15-bit random integers using only integer
   arithmetic.''


*/

/** @pred getrand(- _Key_)


Unify  _Key_ with a term of the form `rand(X,Y,Z)` describing the
current state of the random number generator.


*/


/** @pred random(+ _LOW_, + _HIGH_, - _NUMBER_)

Unify  _Number_ with a number in the range
`[LOW...HIGH)`. If both  _LOW_ and  _HIGH_ are
integers then  _NUMBER_ will also be an integer, otherwise
 _NUMBER_ will be a floating-point number.


*/


/** @defgroup Pseudo_Random Pseudo Random Number Integer Generator
@ingroup library
@{

The following routines produce random non-negative integers in the range
0 .. 2^(w-1) -1, where w is the word size available for integers, e.g.
32 for Intel machines and 64 for Alpha machines. Note that the numbers
generated by this random number generator are repeatable. This generator
was originally written by Allen Van Gelder and is based on Knuth Vol 2.


*/


/** @pred random(- _Number_)


Unify  _Number_ with a floating-point number in the range `[0...1)`.


*/
/** @pred randseq(+ _LENGTH_, + _MAX_, - _Numbers_)


Unify  _Numbers_ with a list of  _LENGTH_ unique random integers
in the range `[1... _MAX_)`.


*/
/** @pred randset(+ _LENGTH_, + _MAX_, - _Numbers_)


Unify  _Numbers_ with an ordered list of  _LENGTH_ unique random
integers in the range `[1... _MAX_)`.


*/
/** @pred setrand(+ _Key_)


Use a term of the form `rand(X,Y,Z)` to set a new state for the
random number generator. The integer `X` must be in the range
`[1...30269)`, the integer `Y` must be in the range
`[1...30307)`, and the integer `Z` must be in the range
`[1...30323)`.




 */
%:- use_module(library(pairs)).
:- use_module(library(lists)).


:- load_foreign_files([yap_random], [], init_random).


%   random(R) binds R to a new random number in [0.0,1.0)

%   random(L, U, R) binds R to a random integer in [L,U)
%   when L and U are integers (note that U will NEVER be generated),
%   or to a random floating number in [L,U) otherwise.

random(L, U, R) :-
	( integer(L), integer(U) ->
	    U > L,
	    random(X),
	    R is L+integer((U-L)*X)
        ;
	    number(L), number(U),
	    U > L,
	    random(X),
	    R is L+((U-L)*X)
	).

/*  There are two versions of this operation.

	randset(K, N, S)

    generates a random set of K integers in the range 1..N.
    The result is an ordered list, such as setof might produce.

	randseq(K, N, L)

    generates a random sequence of K integers, the order is as
    random as we can make it.
*/


randset(K, N, S) :-
	K >= 0,
	K =< N,
	randset(K, N, [], S).


randset(0, _, S, S) :- !.
randset(K, N, Si, So) :-
	random(X),
	X * N < K, !,
	J is K-1,
	M is N-1,
	randset(J, M, [N|Si], So).
randset(K, N, Si, So) :-
	M is N-1,
	randset(K, M, Si, So).


randseq(K, N, S) :-
	randseq(K, N, L, []),
	keysort(L, R),
	strip_keys(R, S).

randseq(0, _, S, S) :- !.
randseq(K, N, [Y-N|Si], So) :-
	random(X),
	X * N < K, !,
	random(Y),
	J is K-1,
	M is N-1,
	randseq(J, M, Si, So).
randseq(K, N, Si, So) :-
	M is N-1,
	randseq(K, M, Si, So).


strip_keys([], []) :- !.
strip_keys([_-K|L], [K|S]) :-
	strip_keys(L, S).

setrand(rand(X,Y,Z)) :-
	integer(X),
	integer(Y),
	integer(Z),
	X > 0,
	X < 30269,
	Y > 0,
	Y < 30307,
	Z > 0,
	Z < 30323,
	setrand(X,Y,Z).

getrand(rand(X,Y,Z)) :-
	getrand(X,Y,Z).
