(ns clojurewerkz.romulan.dsl
  (:import [java.util.concurrent Executors ExecutorService]
           [com.lmax.disruptor EventFactory EventTranslator EventHandler ClaimStrategy WaitStrategy]
           [com.lmax.disruptor.dsl Disruptor]))

;;
;; Implementation
;;

(defn- fn->event-factory
  "Creates a new Disruptor EventFactory that uses provided function"
  [f]
  (reify com.lmax.disruptor.EventFactory
    (newInstance [this]
      (f))))

(defn- fn->event-handler
  "Creates a new Disruptor EventHandler that uses provided function"
  [f]
  (reify com.lmax.disruptor.EventHandler
    (onEvent [this event sequence end-of-batch?]
      (f event sequence end-of-batch?))))

(defn- fn->event-translator
  "Creates a new Disruptor EventTranslator that uses provided function"
  [f]
  (reify com.lmax.disruptor.EventTranslator
    (translateTo [this event sequence]
      (f event sequence))))



;;
;; API
;;

(defprotocol EFFactory
  (^com.lmax.disruptor.EventFactory event-factory [arg] "Creates Disruptor EventFactory from arg"))

(extend-protocol EFFactory
  EventFactory
  (event-factory [^EventFactory arg]
    arg)

  clojure.lang.IFn
  (event-factory [^clojure.lang.IFn arg]
    (fn->event-factory arg)))


(defprotocol EventHandlerFactory
  (^com.lmax.disruptor.EventHandler event-handler [arg] "Creates an EventHandler from arg"))

(extend-protocol EventHandlerFactory
  EventHandler
  (event-handler [arg]
    arg)

  clojure.lang.IFn
  (event-handler [^clojure.lang.IFn arg]
    (fn->event-handler arg)))


(defprotocol EventTranslatorFactory
  (^com.lmax.disruptor.EventTranslator event-translator [arg] "Creates an EventTranslator from arg"))

(extend-protocol EventTranslatorFactory
  EventTranslator
  (event-translator [arg]
    arg)

  clojure.lang.IFn
  (event-translator [^clojure.lang.IFn arg]
    (fn->event-translator arg)))




(defn ^com.lmax.disruptor.ExceptionHandler exception-handler
  "Instantiates a new Disruptor exception handler that uses provided functions (:on-event, :on-start, :on-shutdown)"
  [&{ :keys [on-event on-start on-shutdown] }]
  (reify com.lmax.disruptor.ExceptionHandler
    (handleEventException [this ex sequence event]
      (on-event ex sequence event))
    (handleOnStartException [this ex]
      (on-start ex))
    (handleOnShutdownException [this ex]
      (on-shutdown ex))))


(defn ^com.lmax.disruptor.dsl.Disruptor disruptor
  ([ef ^long ring-buffer-size ^ExecutorService executor]
     (Disruptor. (event-factory ef) ring-buffer-size executor))
  ([ef ^ExecutorService executor ^ClaimStrategy cs ^WaitStrategy ws]
     (Disruptor. (event-factory ef) executor cs ws)))


(defn ^com.lmax.disruptor.dsl.EventHandlerGroup handlers
  [^Disruptor d xs]
  (.handleEventsWith d (into-array EventHandler (map event-handler xs))))

(defn ^com.lmax.disruptor.dsl.EventHandlerGroup then
  [^EventHandlerGroup g xs]
  (.then g (into-array EventHandler (map event-handler xs))))
