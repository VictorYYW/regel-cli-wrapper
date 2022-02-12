# regel-cli-wrapper

This is a command line interface for [REGEL](https://github.com/utopia-group/regel). Note that this repo only include pretrained model for stack overflow benchmark but not the code for training.

## Prerequisite

As REGEL, this repository requires the following:

- [Z3](https://github.com/Z3Prover/z3). Make sure you have Z3 installed with the Java binding. 
- `ant` to compile the java files.
- `python3 ` 3.7
- `java` 1.8.0

Assuming you are in unix-like system like Linux or Mac OS, you may use the following shell script for reference to set up `python3`, `Z3` and `ant`:

```shell
cd ~
wget https://www.python.org/ftp/python/3.7.9/Python-3.7.9.tar.xz
cd Python-3.7.9
./configure
make
make test
sudo make install

cd ~
git clone https://github.com/Z3Prover/z3.git
cd z3
python3 scripts/mk_make.py --java
cd build
make
sudo make install

cd ~
wget https://dlcdn.apache.org//ant/binaries/apache-ant-1.10.12-bin.tar.xz
tar -xf apache-ant-1.10.12-bin.tar.xz
export PATH=$PATH:$PWD/apache-ant-1.10.12/bin
```

Then run the following code snippet to set up the project:

```shell
./pull-dependencies core
./pull-dependencies corenlp
./pull-dependencies freebase
./pull-dependencies tables
ant regex
ant -buildfile resnax-build.xml resnax
javac -cp "lib/*:libregex/*:libsempre/*" src/Main.java
jar cvfm regel-cli-wrapper.jar manifest.txt -C classes/ .
```

The following command will prompt you with a command line interface for regex synthesizer.

```shell
java -jar regel-cli-wrapper.jar -Djava.library.path="lib" -languageAnalyzer corenlp.CoreNLPAnalyzer -parser SketchParser -Grammar.inPaths "dataset/regex.grammar" -SimpleLexicon.inPaths dataset/regex.lexicon -FeatureExtractor.featureDomains rule span context bigram phrase-category -Builder.inParamsPath "pretrained_models/pretrained_so/params" -Parser.beamSize 200 -log.stdout false
```
