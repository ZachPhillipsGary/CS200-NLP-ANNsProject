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
inputdim = 1
#Create ClassificationDataSet
ds = ClassificationDataSet(inputdim, nb_classes=2, class_labels=['Correct','Incorrect'])


#At this point, NPChunker has been trained and is ready to go 
"""
converttoCFG() -- generates CFG for each sentense in the input corpus file
@param{String} -> file name of plain text
pre:
NPChunker trained and ClassificationDataSet initialized 
post:
returns NLTK CFG
"""
def converttoCFG(file):
	with open(file, 'r') as f:
		for line in f:
			tokens = line.split(".") #break file into sentences
			for sentence in tokens:
				tree = NPChunker.parse(nltk.pos_tag(sentence)) 
				print tree
				return tree

"""
preProcess() -- populates SupervisedDataSet
@param{String} -> file name of plain text
@param{String} -> category to place set into ('Correct' or 'Incorrect')
pre:
NPChunker trained and ClassificationDataSet initialized 
post:
ClassificationDataSet filled with CFGs created from file by NPChunker
"""
def preProcess(file, type):
	ds.appendLinked([nltk.toVector(converttoCFG(file))], [type])


#train the network on valid data
#preProcess('word.txt','Correct')
brownCorpora = nltk.corpus.brown.fileids()
for corpus in brownCorpora:
	fo = open(str(corpus), "w")
	fo.write(" ".join(nltk.corpus.brown.words(corpus)))
	fo.seek(0)
	fo.close()
	preProcess(corpus,0)
	pass
#now add the modified brown corpus 
for corpus in brownCorpora:
	fo = open(str(corpus), "w")
	#append "modified" to the corpus file names to get edited versions
	preProcess(corpus+"modified",'Correct')
	pass
#Create the ANN
n = FeedForwardNetwork()
#Add layers
inLayer = LinearLayer(2)
hiddenLayer = SigmoidLayer(3)
outLayer = LinearLayer(1)
n.addInputModule(inLayer)
n.addModule(hiddenLayer)
n.addOutputModule(outLayer)
#connect layers together
in_to_hidden = FullConnection(inLayer, hiddenLayer)
hidden_to_out = FullConnection(hiddenLayer, outLayer)
#add layers to network
n.addConnection(in_to_hidden)
n.addConnection(hidden_to_out)
n.sortModules()
tstdata, trndata = ds.splitWithProportion( 0.25 )
fnn = buildNetwork( trndata.indim, 5, trndata.outdim, outclass=SoftmaxLayer )
#ready the trainer
trainer = BackpropTrainer( fnn, dataset=trndata, momentum=0.1, verbose=True, weightdecay=0.01)
#Start the training iterations.
for i in range(20):
	 trainer.trainEpochs( 1 )
#get results
trnresult = percentError( trainer.testOnClassData(),                              trndata['class'] )
tstresult = percentError( trainer.testOnClassData(dataset=tstdata ), tstdata['class'] )
print "epoch: %4d" % trainer.totalepochs, \
	"  train error: %5.2f%%" % trnresult, \
	"  test error: %5.2f%%" % tstresult
#TODO: add plot
