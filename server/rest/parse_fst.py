import os
import errno
import wave
import fst
import codecs
import fnmatch

def wst2dict(wst_path, encoding='utf-8'):
    ''' Loads word symbol table (WST) to python dictionary.
    Args:
        wst_path(str): path to file with integer and word per line
    Returns:
        Python dictionary which maps int ids to words
    '''
    with codecs.open(wst_path, encoding=encoding) as r:
        # split removes empty and white space only splits
        line_arr = [line.split() for line in r.readlines()]
        d = dict([])
        for arr in line_arr:
            assert len(arr) == 2, 'Word Symbol Table should have 2 records on each row'
            # WST format:  WORD  NUMBER  ...we store d[NUMBER] = WORD
            d[int(arr[1])] = arr[0]
        return d

def _posibilities(fst, fstNumState, words, posibilities, n):
    fstState = _generatorIndex(fst.states, fstNumState)
    if not fstState: #did not find state with fstNumState
        return
    while len(posibilities) <= n:
        posibilities.append(set())
    if not isinstance(posibilities[n], set):
        posibilities[n] = set() 
    for i in fstState.arcs:
        next_n = n + 1
        if(fnmatch.fnmatch(words[i.ilabel], "_*")):
             # this is something special do not add it to posibilities
              next_n = n
        else:
            posibilities[n].add(words[i.ilabel]);
        if(not fstState.final):
            _posibilities(fst, i.nextstate, words, posibilities, next_n);

def posibilities(fstVector,words):
    ''' Return list of posibilities for every word in fstVector '''
    posibilities = [];
    _posibilities(fstVector,fstVector.start, words, posibilities, 0)
    for i in posibilities:
	# remove empty sets
         if len(i) == 0:
             posibilities.remove(i)
    return posibilities

def _generatorIndex(generator, index):
     for item in generator:
         index = index - 1
         if (index < 0):
             return item

def test():
    f = fst.read("../data/vad-2013-09-26-20-31-51.384464.fst")
    f.remove_epsilon()
    words = wst2dict("../data/words.txt")
    print(posibilities(f, words))


test()
