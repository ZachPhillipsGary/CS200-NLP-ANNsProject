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
from pybrain.structure 			 import LinearLayer, SigmoidLayer
from pybrain.structure 			 import FeedForwardNetwork
from pybrain.structure 			 import FullConnection
#from pylab 						 import ion, ioff, figure, draw, contourf, clf, show, hold, plot
from scipy 					     import diag, arange, meshgrid, where
from numpy.random 				 import multivariate_normal
#End includes
class ChunkParser(nltk.ChunkParserI):
	 def __init__(self, train):
		train_data = [[(t,c) for w,t,c in nltk.chunk.tree2conlltags(sent)] for sent in train] #get training data
	 	self.tagger = nltk.TrigramTagger(train_data) ##apply tagger (chooses a token's tag based its word string and on the preceeding two words' tags)
	 	#see http://nltk.sourceforge.net/doc/api/nltk.tag.sequential.TrigramTagger-class.html
	 def parse(self, sentence):
		pos_tags = [pos for (word,pos) in sentence]
		tagged_pos_tags = self.tagger.tag(pos_tags)
		chunktags = [chunktag for (pos, chunktag) in tagged_pos_tags]
		conlltags = [(word, pos, chunktag) for ((word,pos),chunktag) in zip(sentence, chunktags)] 
		return nltk.chunk.conlltags2tree(conlltags) #find and return non-overlapping groups
#create training and testing sets for the chunker 
test_sents = conll2000.chunked_sents('test.txt', chunk_types=['NP'])
train_sents = conll2000.chunked_sents('train.txt', chunk_types=['NP'])
#train the chunker
NPChunker = ChunkParser(train_sents)
#echo out results
print NPChunker.evaluate(test_sents)


NPChunker.parse(nltk.pos_tag(nltk.word_tokenize("And now for something completely different"))).draw()