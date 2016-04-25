#ann.py
import os
import nltk #for general NLP and classifiers
import pybrain # for ANNs
import random #for selection of verbs to alter
import en #conjugator service
import urllib2  #for HTTP requests
import xml.etree.ElementTree as ET #for xml parsing
from nltk.corpus import brown
from pybrain.datasets import SupervisedDataSet
#tokenize and tag
tagged = nltk.sent_tokenize(sentences.strip())
tagged = [nltk.word_tokenize(sentence) for sentence in tagged]
tagged = [nltk.pos_tag(sentence) for sentence in tagged]
#^ Source: http://www.nltk.org/book/

