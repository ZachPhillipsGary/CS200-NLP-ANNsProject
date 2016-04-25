#Begin includes
import nltk
from nltk.corpus 				 import brown
from nltk 						 import word_tokenize
from nltk.corpus 				 import conll2000
from nltk.corpus 				 import brown
from nltk.chunk 				 import *
from nltk.chunk.util             import *
from nltk.chunk.regexp           import *
from nltk import Tree
#pybrain includes
from pybrain.datasets            import ClassificationDataSet
from pybrain.utilities           import percentError
from pybrain.tools.shortcuts     import buildNetwork
from pybrain.supervised.trainers import BackpropTrainer
#from pylab 						 import ion, ioff, figure, draw, contourf, clf, show, hold, plot
from scipy 					     import diag, arange, meshgrid, where
from numpy.random 				 import multivariate_normal
#End includes
"""
First, we need to indentify chuck patterns 
Thus, we need a system to learn tag patterns (regular expressions to find sentences based on tags).
Since this isn't the focus of this project, a preexisting solution will be used. The solution in question uses NLTK's
builtin 
"""
#Beginning code from https://www.eecis.udel.edu/~trnka/CISC889-11S/ Chuck parser lecture notes

class ChunkParser(nltk.ChunkParserI):
	 def __init__(self, train):
		train_data = [[(t,c) for w,t,c in nltk.chunk.tree2conlltags(sent)] for sent in train]
	 	self.tagger = nltk.TrigramTagger(train_data)

	 def parse(self, sentence):
		pos_tags = [pos for (word,pos) in sentence]
		tagged_pos_tags = self.tagger.tag(pos_tags)
		chunktags = [chunktag for (pos, chunktag) in tagged_pos_tags]
		conlltags = [(word, pos, chunktag) for ((word,pos),chunktag) in zip(sentence, chunktags)] 
		return nltk.chunk.conlltags2tree(conlltags)


#create training and testing sets for the chunker 
test_sents = conll2000.chunked_sents('test.txt', chunk_types=['NP'])
train_sents = conll2000.chunked_sents('train.txt', chunk_types=['NP'])
#train the chunker
NPChunker = ChunkParser(train_sents)
#echo out results
print NPChunker.evaluate(test_sents)
#End of code from https://www.eecis.udel.edu/~trnka/CISC889-11S/

#At this point, NPChunker has been trained and is ready to go 
"""
converttoCFG() -- generates CFG for each sentense in the input corpus file
@param{String} -> file name of plain text
pre:
NPChunker trained and initialized 
post:
ClassificationDataSet populated with sentence CFG trees
"""
def converttoCFG(file):
	with open(file, 'r') as f:
		for line in f:
			tokens = line.split(".")
			for sentence in tokens:
				tree = NPChunker.parse(nltk.pos_tag(sentence)) 
			
