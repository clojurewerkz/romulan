(ns clojurewerkz.romulan.dsl-test
  (:use clojure.test clojurewerkz.romulan.dsl)
  (:import [java.util.concurrent Executors ExecutorService CountDownLatch]
           [com.lmax.disruptor EventHandler EventTranslator ExceptionHandler SingleThreadedClaimStrategy BlockingWaitStrategy]
           [com.lmax.disruptor.dsl Disruptor]))

(deftest test-event-factory
  (let [f  (constantly 42)
        ef (event-factory f)]
    (is (instance? com.lmax.disruptor.EventFactory ef))
    (is (= 42 (.newInstance ef)))))

(deftest test-handler-factory
  (let [state (atom {})
        fn    (fn [event sequence end-of-batch?]
                (swap! state assoc :event event :sequence sequence :eob end-of-batch?))
        eh      (event-handler fn)]
    (is (instance? EventHandler eh))
    (.onEvent eh { :type "dummy" } 50 true)
    (is (= "dummy" (get-in @state [:event :type])))
    (is (= 50      (:sequence @state)))
    (is (:eob @state))))

(deftest test-event-translator
  (let [et (event-translator (fn [x _] x))]
    (is (instance? com.lmax.disruptor.EventTranslator et))
    (are [input output] (is (= output (.translateTo et input 0)))
         1     1
         "2"   "2"
         3.0   3.0
         4/5   4/5
         'five 'five
         :six  :six)))

(deftest test-exception-handler-factory
  (letfn [(event-ehf    [ex l evt])
          (startup-ehf  [ex])
          (shutdown-ehf [ex])]
    (is (instance? ExceptionHandler (exception-handler :on-event event-ehf
                                                       :on-start startup-ehf
                                                       :on-shutdown shutdown-ehf)))))

(deftest test-basic-dsl-example1
  (let [^Disruptor d (disruptor (event-factory (constantly 99))
                                (Executors/newFixedThreadPool 2)
                                (SingleThreadedClaimStrategy. 4)
                                (BlockingWaitStrategy.))]
    (.start d)
    (.shutdown d)))

(deftest test-basic-dsl-example2
  (let [^Disruptor d (disruptor (constantly 99)
                                256
                                (Executors/newFixedThreadPool 2))]
    (.start d)
    (.shutdown d)))


(deftest test-publishing-and-handling-events-using-java-interop-example1
  (let [n     87
        latch (CountDownLatch. n)
        eh    (event-handler (fn [event sequence end-of-batch?]
                               ))
        et    (event-translator (fn [x _]
                                  (.countDown latch)
                                  x))
        ^Disruptor d  (disruptor (constantly 99)
                                 256
                                 (Executors/newFixedThreadPool 2))]
    (.handleEventsWith d (into-array EventHandler [eh]))
    (.start d)
    (dotimes [i n]
      (.publishEvent d et))
    (.await latch)
    (.shutdown d)))

(deftest test-publishing-and-handling-events-using-java-interop-example2
  (let [n      87
        latch1 (CountDownLatch. n)
        latch2 (CountDownLatch. n)
        eh1    (event-handler (fn [event sequence end-of-batch?]
                                (.countDown latch1)))
        eh2    (event-handler (fn [event sequence end-of-batch?]
                                (.countDown latch2)))        
        et     (event-translator (fn [x _]
                                   x))
        ^Disruptor d  (disruptor (constantly 99)
                                 256
                                 (Executors/newFixedThreadPool 2))]
    (-> d (handle-events-with eh1 eh2))
    (.start d)
    (dotimes [i n]
      (.publishEvent d et))
    (.await latch)
    (.shutdown d)))
