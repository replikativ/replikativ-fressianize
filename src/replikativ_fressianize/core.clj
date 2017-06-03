(ns replikativ-fressianize.core
  (:require [hasch.core :refer [uuid]]
            [superv.async :refer [go-try <?]]
            [konserve.core :as k]
            [konserve.serializers :refer [fressian-serializer]]
            [konserve.protocols :refer [-serialize
                                        -deserialize]]
            [replikativ.environ :refer [store-blob-trans-value]])
  (:import [java.io ByteArrayOutputStream]))

(defn- to-bytearray [v]
  (let [boas (ByteArrayOutputStream.)]
    (-serialize (fressian-serializer)
                boas
                (atom {}) v)
    (.toByteArray boas)))


(defn fressianize [txs]
  (mapv
   (fn [[tx-fn params]]
     (let [params-blob (to-bytearray params)
           id (uuid params-blob)]
       [[store-blob-trans-value params-blob]
        [tx-fn id]]))
   txs))

(comment
  (fressianize [['add-tweets {:foo :bar}]]))


(defn unfressianize
  ([S store id]
   (unfressianize S store id (:read-handlers store)))
  ([S store id read-handlers]
   (go-try S
           (let [params
                 (<? S (k/bget store id
                               #(-deserialize (fressian-serializer) read-handlers
                                              (:input-stream %))))]
             params))))

