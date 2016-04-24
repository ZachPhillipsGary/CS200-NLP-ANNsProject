#being includes
import nltk
import os
from nltk.corpus import PlaintextCorpusReader
from nltk.corpus import brown
def generateError(word):
	return word
"""randomly_alter_verbs()
@Param {Number} percentage - percentage of verbs from corpus to alter
"""
def randomly_alter_verbs(size):
	return size
""" end of functions """
words = brown.tagged_words()
file = open("modifedBROWN.txt", "wb")
for word in words:
    if word.startswith('V'):
        file.write(generateError(word)+" ")
file.close()
corpus_root = './'
 """
 if the ./ dir contains the file my_corpus.txt, then you 
 can view say all the words it by doing this 
 """
newcorpus.words('my_corpus.txt')