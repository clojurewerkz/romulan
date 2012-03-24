# Romulan, LMAX Disruptor in Clojure Embrace

ClojureWerkz Romulan is a Clojure DSL for [LMAX Disruptor](http://code.google.com/p/disruptor/).


## Documentation & Examples

ClojureWerkz Romulan is a very young project and until 1.0 is released and documentation guides are written,
it may be challenging to use for anyone except the author. For code examples, see our test
suite.

Once the library matures, we will update this document.

## Community

Once APIs stabilize, we will start a mailing list.

To subscribe for announcements of releases, important changes and so on, please follow [@ClojureWerkz](https://twitter.com/#!/clojurewerkz) on Twitter.


## This is a Work In Progress

This is a young project that is still very much a work in progress.



## Maven Artifacts

### Snapshots

If you are comfortable with using snapshots, snapshot artifacts are [released to Clojars](https://clojars.org/clojurewerkz/romulan) every few days.

With Leiningen:

    [clojurewerkz/romulan "0.1.0-SNAPSHOT"]


With Maven:

    <dependency>
      <groupId>clojurewerkz</groupId>
      <artifactId>romulan</artifactId>
      <version>0.1.0-SNAPSHOT</version>
    </dependency>


## Supported Clojure versions

Romulan is built from the ground up for Clojure 1.3 and up.


## Continuous Integration

[![Continuous Integration status](https://secure.travis-ci.org/clojurewerkz/romulan.png)](http://travis-ci.org/clojurewerkz/romulan)

CI is hosted by [travis-ci.org](http://travis-ci.org)


## Development

Romulan uses [Leiningen 2](https://github.com/technomancy/leiningen/blob/master/doc/TUTORIAL.md). Make sure you have it installed and then run tests against Clojure 1.3.0 and 1.4.0[-beta5] using

    lein2 all test

Then create a branch and make your changes on it. Once you are done with your changes and all tests pass, submit
a pull request on Github.


## License

Copyright © 2012 Michael S. Klishin, Alex Petrov

Distributed under the Eclipse Public License, the same as Clojure.
