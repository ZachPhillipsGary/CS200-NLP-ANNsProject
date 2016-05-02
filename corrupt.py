#corrupt.py -- introduces errors into a copy of the brown corpus.
import os
import nltk #for general NLP and classifiers
# import pybrain for ANNs
import random #for selection of verbs to alter
#import en #nodebox conjugator service (NOT USED)
import urllib2  #for HTTP requests
import xml.etree.ElementTree as ET #for xml parsing
from nltk.corpus import brown
from random import randint #for random number generator
#start up MorphAdorner   
print "Please run cd dependencies/maserver-1.0.0/; ./runmaserver before calling this script" 
""" 
---preprocesssing phase---
Introduce errors into each file in the brown corpus and save our modified versions inside a new directory

"""
def is_noun(tag):
    return tag in ['NN', 'NNS', 'NNP', 'NNPS']


def is_verb(tag):
    return tag in ['VB', 'VBD', 'VBG', 'VBN', 'VBP', 'VBZ']


def is_adverb(tag):
    return tag in ['RB', 'RBR', 'RBS']


def is_adjective(tag):
    return tag in ['JJ', 'JJR', 'JJS']



"""
NOT USED :(

NodeboxmodifyVerb()
Orginal implementation of verb tense altering function, uses the Nodebox English Linguistics library.
Sadly this package doesn't fail gracefully and only works on a very limited set of verbs (try with either [u'investigate', 'VB']
or [u'find', 'VB'])
@param(tuple) tagged verb to be altered ('verb','VB')
pre: n/a
post: returns inputted verb
"""
def NodeboxmodifyVerb(v):
	verbObject = list(v) #convert into list so we can modify it
	##double check that we got a verb
	if verbObject[1] == 'VB':
		currentTense = en.verb.tense(verbObject[0]) #get current tense of verb
		newTense = random.choice(en.verb.tenses())
		if newTense != currentTense:
			if newTense == 'past':
				verbObject[0] = en.verb.past(str(verbObject[0]))
			if newTense == '3rd singular present':
				verbObject[0] = en.verb.present(str(verbObject[0]), person=3, negate=False)
			if newTense == 'past participle':
				verbObject[0] = en.verb.present_participle(str(verbObject[0]))
			if newTense == 'infinitive':
				verbObject[0] = en.verb.infinitive(str(verbObject[0]))
			if newTense == 'present participle':
				verbObject[0] = en.verb.past_participle(str(verbObject[0]))
			if newTense == '1st singular present':
				verbObject[0] = en.verb.present(str(verbObject[0]), person=1, negate=False)
			if newTense == '1st singular past':
				verbObject[0] = en.verb.past(str(verbObject[0]),person=1,negate=False)
			if newTense == 'past plural':
				verbObject[0] = en.verb.past(str(verbObject[0]),person='plural',negate=False)
			if newTense == '2nd singular present':
				verbObject[0] = en.verb.present(str(verbObject[0]), person=2, negate=False)
			if newTense == '2nd singular past':
				verbObject[0] = en.verb.past(str(verbObject[0]),person=2,negate=False)
			if newTense == '3rd singular past':
				verbObject[0] = en.verb.past(str(verbObject[0]),person=3,negate=False)
			if newTense == 'present plural':
				verbObject[0] = en.verb.present(str(verbObject[0]), person='plural', negate=False)
			return tuple(verbObject)
		pass
"""

#create a dictionary for fast verb lookups 

"""
verbTable = {}

"""
modifyVerb() -- replaces verb with same verb in a random tense 

@param(tuple) tagged verb to be altered ('verb','VB')
pre: n/a
post: returns inputted verb as tuple
"""
def modifyVerb(v):
	verb =  list(v) #convert tuple into list so we make changes to it
	print verb
	if verb[0] not in verbTable:
		#load verb
		tense = "present"
		baseURL = "http://localhost:8182/verbconjugator?infinitive="+verb[0]+"&verbTense="+tense+"&media=xml&conjugate=Conjugate"
		print baseURL
		queryResult = urllib2.urlopen(baseURL).read()
		requestResults = ET.fromstring(queryResult)
		formattedResult = {} #create dictionary with XML results
		for child in requestResults:
  	 		 formattedResult[child.tag] = child.text
  	 	verbTable[verb[0]] = formattedResult
  	 	#finished loading verb data (if needed), now modify the damn thing
  	 	possibleTenses = ["firstPersonSingular","secondPersonSingular","thirdPersonSingular","firstPersonPlural","secondPersonPlural","thirdPersonPlural"] 
  		newTense = random.choice(possibleTenses)
  		conjugations = verbTable[str(verb[0])] #get conjugations from hashtable
  		verb[0] = str(conjugations[str(newTense)])
  		#append modification flag to word
  		verb[1] = verb[1]+'+mod'
  		print verb 
  		return tuple(verb) #return result as immutable tuple
"""
randomly_alter_verbs(persentage) -- iterates through the brown corpus and changes the tense of a particular persentage indicated in by the function param

@param(Number) persentage of verbs to modifed
pre: NLTK imported, MorphAdorner running locally on port 8182
post: persentage% of verbs modified, modified brown corpus copied to current directory
"""
def randomly_alter_verbs(persentage):
	#get files in the brown corpus
	Brownfiles = nltk.corpus.brown.fileids()
	for file in Brownfiles:
		#create a new file with the format
		corpusFile = nltk.corpus.brown.words(file)
		fileObject = open(str(file)+"modified", "w")
		#get tags
		taggedFile = nltk.pos_tag(corpusFile)
		#now find verbs
		for word in taggedFile:
			if is_verb(word[1]) == True:
				#determine if we should modify this verb
				if randint(1,10) <= persentage:
					getword = modifyVerb(word)
				else:
					getword = word #don't modify
			else:
				getword = word
			if type(getword) is tuple:
				fileObject.write( str(getword[0])+'/'+str(getword[1] ) )
			pass
		#write modifed version of corpus subset to file
		fileObject.close()
		print 'modified verbs in '+str(file)
		pass
persentageToModify = input('Enter persentage of verbs to modify: ');
randomly_alter_verbs(persentageToModify);