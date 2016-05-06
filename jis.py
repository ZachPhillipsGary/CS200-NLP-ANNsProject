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
from nltk import NaiveBayesClassifier as nbc
from nltk.tokenize import word_tokenize
from itertools import chain
import os
from nltk.parse import stanford
#End includes
#Based on tutorial at https://www.eecis.udel.edu/~trnka/CISC889-11S/
##Noun part classifier
class NounChunkParser(nltk.ChunkParserI):
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
test_sents = conll2000.chunked_sents('test.txt', chunk_types=['NP']) #from WSJ corpus
train_sents = conll2000.chunked_sents('train.txt', chunk_types=['NP']) #get noun types 
#train the chunker
NPChunker = NounChunkParser(train_sents)
#echo out results
print NPChunker.evaluate(test_sents)
def process(sentence):
	modified =  False
	#first convert string into list of tuples (word, tag)
	annotatedSentence = [nltk.tag.str2tuple(t) for t in sentence.split()] #from http://www.nltk.org/book/ch05.html
	#find verbs
	for word in annotatedSentence:
		if word[1].startswith('VB', 0, len(word)):
			if word[1].endswith('+mod'):
					print word
				#the verb or verbs in this sentence have been modified, flag it as incorrect
					modified =  False
			pass
		pass
		#remove tags from list
		bareSent = [word[0] for word in annotatedSentence]
		sentString = ' '.join(bareSent) 
		if modified == True:
			return tuple([sentString,'incorrect'])
		else:
			return tuple([sentString,'correct'])
	pass

test_sentence = raw_input("Type an English sentence to chunk and display:")
NPChunker.parse(nltk.pos_tag(nltk.word_tokenize(test_sentence))).draw()
def trainClassifier():
	"""
	Chunker trained, ready to parse BROWN corpus
	"""
	corpus = [] #populate with sentence/class tuples using process
	print("Ready to load data!")
	#folder = raw_input('Enter path to modified corpus:');
	#get files in the brown corpus
	Brownfiles = ['ca01','ca02','ca03'] #train with subset of BROWN for testing
	for file in Brownfiles:
		f = open(file+"modified", 'r') #we want to read our modified copy of the BROWN corpus
		for line in f:
	  			corpus.append(process(line))
		f.close()
		pass2
	vocabulary = set(chain(*[word_tokenize(i[0].lower()) for i in corpus])) 
	#generate features from corpus
	feature_set = [({i:(i in word_tokenize(sentence.lower())) for i in vocabulary},tag) for sentence, tag in corpus]

	classifier = nbc.train(feature_set)
	#begin interactive demo
	#break test sent down into featres
	featurized_test_sentence =   [({i:(i in word_tokenize(test_sentence.lower())) for i in vocabulary},tag) for sentence, tag in corpus]
	print classifier.classify(featurized_test_sentence)
trainClassifier()