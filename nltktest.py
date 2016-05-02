from nltk import NaiveBayesClassifier as nbc
from nltk.tokenize import word_tokenize
from itertools import chain
import nltk
training_data = [('I love this sandwich.', 'pos'),
('This is an amazing place!', 'pos'),
('I feel very good about these beers.', 'pos'),
('This is my best work.', 'pos'),
("What an awesome view", 'pos'),
('I do not like this restaurant', 'neg'),
('I am tired of this stuff.', 'neg'),
("I can't deal with this", 'neg'),
('He is my sworn enemy!', 'neg'),
('My boss is horrible.', 'neg')]

test_set = [('I like cats.', 'pos'),
('This is awesome!', 'pos'),
('I enjoy wine and cheese.', 'pos'),
('This work sucks.', 'neg'),
("What an annoying person", 'pos'),
('I hate dogs!', 'neg'),
('I wish he would go away.', 'neg'),
("I don't enjoy riding in boats", 'neg'),
('He is a terrible person!', 'neg'),
('My brother is not very nice.', 'neg')]


vocabulary = set(chain(*[word_tokenize(i[0].lower()) for i in training_data]))

feature_set = [({i:(i in word_tokenize(sentence.lower())) for i in vocabulary},tag) for sentence, tag in training_data]

classifier = nbc.train(feature_set)

test_sentence = "This is the best band I've ever heard!"
featurized_test_sentence =  {i:(i in word_tokenize(test_sentence.lower())) for i in vocabulary}

print "test_sent:",test_sentence
print "tag:",classifier.classify(featurized_test_sentence)
classifier.show_most_informative_features(5)
#print(nltk.classify.accuracy(classifier, test_set))
