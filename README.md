# replikativ-fressianize <a href="https://gitter.im/replikativ/replikativ?utm_source=badge&amp;utm_medium=badge&amp;utm_campaign=pr-badge&amp;utm_content=badge"><img src="https://camo.githubusercontent.com/da2edb525cde1455a622c58c0effc3a90b9a181c/68747470733a2f2f6261646765732e6769747465722e696d2f4a6f696e253230436861742e737667" alt="Gitter" data-canonical-src="https://badges.gitter.im/Join%20Chat.svg" style="max-width:100%;"></a>

This library contains two helper functions to (de)serialize transaction
parameters in [replikativ](http://replikativ.io). This is necessary if you want
to store large amounts of data in replikativ. replikativ currently does not
decide on a serialization format for you, but treats all data as edn in its
code. The performance differences and the payload of additional serialization
formats like fressian are not necessary in small data cases and harm the cljs
experience. On JavaScript runtimes transit+json is still faster than fressian,
although
an [optimized version exists](https://github.com/spinningtopsofdoom/longshi).

If you want to use nippy or some other binary storage format (e.g. for high
throughput on the JVM), you can write similar wrapper functions around
replikativ's binary blob protocol.

## Usage

Add this to your project.
[![Clojars Project](http://clojars.org/io.replikativ/replikativ-fressianize/latest-version.svg)](http://clojars.org/io.replikativ/replikativ-fressianize)

You just wrap the data of your transaction:

### Serialization

~~~clojure
(<? S (cs/transact! stage [user cdvcs-id]
                           (fressianize [['add-tweets tweets]])))

~~~

~~~clojure
(def eval-fns
  {'add-tweets (fn [S old id]
                 (go-try S
                   (let [tweets (<? S (unfressianize S client-store id))]
                     (swap! old into (map :text tweets)))
                   old))})
~~~

The examples are taken from the [twitter collector](https://github.com/replikativ/twitter-collector).

## License

Copyright © 2017 Christian Weilbach

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
