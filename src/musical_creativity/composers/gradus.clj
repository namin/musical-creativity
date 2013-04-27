(ns musical-creativity.composers.gradus
  (:require
   [clojure.math.numeric-tower :as math]))

(def major-scale '(36 38 40 41 43 45 47 48 50 52 53 55
                   57 59 60 62 64 65 67 69 71 72 74 76
                   77 79 81 83 84 86 88 89 91 93 95 96))

(def illegal-verticals (atom '(0 1 2 5 6 10 11 13 14 17 18 22 23 25 26 29 30 34 35 -1 -2 -3 -4 -5 -6 -7 -8)))
(def illegal-parallel-motions (atom '((7 7)(12 12)(19 19)(24 24))))
(def illegal-double-skips (atom '((3 3)(3 4)(3 -3)(3 -4)(-3 -3)(-3 -4)(-3 3)(-3 4)
                                 (4 3)(4 4)(4 -3)(4 -4)(-4 -3)(-4 -4)(-4 3)(-4 4))))
(def direct-fifths-and-octaves (atom '((9 7)(8 7)(21 19)(20 19))))

(def solution (atom []))
(def counterpoint [])

(def save-voices (atom []))
(def rules       (atom []))
(def save-rules  (atom []))

(def *seed-note* (atom 60))

(def seed-notes '(64 62 59 57 55 60) )
(def backtrack [])
(def *cantus-firmus* (atom [69 71 72 76 74 72 74 72 71 69]))

(def new-line (atom []))

(def *print-state* (atom true))
(def *auto-goals* (atom []))
(def saved-templates (atom []))

(def c1 36)
(def d1 38)
(def e1 40)
(def f1 41)
(def g1 43)
(def a1 45)
(def b1 47)
(def c2 48)
(def d2 50)
(def e2 52)
(def f2 53)
(def g2 55)
(def a2 57)
(def b2 59)
(def c3 60)
(def d3 62)
(def e3 64)
(def f3 65)
(def g3 67)
(def a3 69)
(def b3 71)
(def c4 72)
(def d4 74)
(def e4 76)
(def f4 77)
(def g4 79)
(def a4 81)
(def b4 83)
(def c5 84)
(def d5 86)
(def e5 88)
(def f5 89)
(def g5 91)
(def a5 93)
(def b5 95)
(def c5 96)

(def list-of-notes '(c1 d1 e1 f1 g1 a1 b1 c2 d2 e2 f2 g2 a2 b2 c3 d3 e3 f3 g3 a3 b3 c4 d4 e4 f4 g4 a4 b4 c5 d5
                        e5 f5 g5 a5 b5 c5) )
(def *look-ahead* (atom []))
(def temporary-rules (atom []))
(def last-cantus-firmus (atom []))
(def past-model-count (atom []))
(def models
  '(((72 71 74 72 71 69 67 69) (64 67 65 64 62 65 64 60))
    ((72 71 74 72 71 69 67 69) (57 55 53 57 55 53 55 53))
    ((72 71 74 72 71 69 67 69) (57 55 53 52 50 53 52 48))
    ((72 71 74 72 71 69 67 69) (64 67 65 64 67 65 64 60))
    ((69 71 72 69 71 72 74 77 76 74 72) (57 55 52 53 55 57 55 57 55 59 57))
    ((69 71 72 69 71 72 74 77 76 74 72) (57 55 52 53 55 57 55 53 55 53 52))
    ((69 71 72 69 71 72 74 77 76 74 72) (57 55 52 53 55 57 55 53 55 59 57))
    ((69 71 72 69 71 72 74 77 76 74 72) (57 55 52 53 55 57 55 57 60 59 60))
    ((69 71 72 69 71 72 74 77 76 74 72) (57 55 52 53 55 57 55 57 60 59 57))
    ((72 71 69 67 69 72 71 72) (64 62 60 64 62 60 62 64))
    ((72 71 69 67 69 72 71 72) (64 62 65 64 65 64 67 65))
    ((72 71 69 67 69 72 71 72) (57 59 60 64 62 60 62 64))
    ((72 71 69 67 69 72 71 72) (57 55 53 55 53 52 50 48))
    ((72 71 69 67 69 72 71 72) (64 62 65 64 65 64 62 64))
    ((72 71 69 67 69 72 71 72) (64 67 65 64 62 60 62 64))
    ((72 71 69 67 69 72 71 72) (57 59 60 64 62 64 67 65))
    ((72 71 69 67 69 72 71 72) (57 55 53 55 53 52 55 53))
    ((72 71 69 67 69 72 71 72) (64 62 65 64 62 60 62 60))
    ((72 71 69 67 69 72 71 72) (64 62 60 64 62 64 67 65))
    ((72 71 69 67 69 72 71 72) (64 67 65 64 62 64 67 65))
    ((72 71 69 67 69 72 71 72) (57 55 53 55 53 52 50 52))
    ((72 71 69 67 69 72 71 72) (64 67 65 64 62 64 62 60))
    ((72 71 69 67 69 72 71 72) (64 67 65 64 62 60 62 60))
    ((72 71 69 67 69 72 71 72) (64 62 60 64 62 64 62 60))
    ((69 71 72 76 74 72 71 72 74 72) (57 55 57 55 53 57 55 57 55 57))
    ((69 71 72 76 74 72 71 72 74 72) (57 55 57 55 53 57 55 52 53 57))
    ((69 71 72 76 74 72 71 72 74 72) (57 55 57 55 53 57 55 53 50 52))
    ((69 71 72 76 74 72 71 72 74 72) (57 55 57 55 59 57 59 57 59 57))
    ((69 71 72 76 74 72 71 72 74 72) (57 55 57 55 59 57 59 57 55 52))
    ((69 71 72 76 74 72 71 72 74 72) (57 55 53 52 53 52 50 48 47 45))
    ((69 71 72 76 74 72 71 72 74 72) (57 55 57 55 53 52 55 53 50 52))
    ((69 71 69 72 71 74 72 71 69) (57 55 53 52 55 53 57 55 57))
    ((69 71 69 72 71 74 72 71 69) (57 55 53 52 55 53 57 55 53))
    ((69 71 69 72 71 74 72 71 69) (57 55 53 52 55 53 52 50 53))
    ((69 71 72 76 74 72 74 72 71 69) (57 55 53 55 53 57 55 57 59 60))
    ((69 71 72 76 74 72 74 72 71 69) (57 55 57 55 53 52 50 52 50 53))
    ((69 71 72 76 74 72 74 72 71 69) (57 55 53 55 53 57 55 57 59 62))
    ((69 71 72 76 74 72 74 72 71 69) (57 55 53 52 53 52 53 57 55 57))
    ((69 71 72 76 74 72 74 72 71 69) (57 55 53 55 59 57 55 57 59 62))
    ((69 71 72 76 74 72 74 72 71 69) (57 55 53 52 53 52 50 52 55 53))
    ((69 71 72 76 74 72 74 72 71 69) (57 55 57 55 53 52 50 52 55 53))
    ((69 71 72 76 74 72 74 72 71 69) (57 55 53 52 53 57 55 57 55 57))
    ((69 71 72 76 74 72 74 72 71 69) (57 55 53 55 53 57 55 57 55 53))
    ((69 71 72 76 74 72 74 72 71 69) (57 55 53 55 59 57 55 57 55 53))
    ((69 71 72 76 74 72 74 72 71 69) (57 55 57 55 59 57 59 60 62 65))
    ((69 71 72 76 74 72 74 72 71 69) (57 55 57 55 59 57 59 60 62 60))
    ((69 71 69 72 71 74 72 71 69) (57 55 53 52 55 53 52 55 53))
    ((69 71 72 76 74 72 71 72 74 72) (57 55 57 55 59 57 55 53 50 52))
    ((69 71 72 74 71 72 74 72) (57 55 57 53 55 53 50 52))
    ((69 71 72 69 71 72 74 77 76 74 72) (57 55 52 53 55 57 55 53 55 53 57))))

(defn return-counts [templates]
  "simply adds the count of occurances to the beginning of each member of its arg."
  (if (empty? templates)
    []
    (cons (list (count (filter #(= % (first templates)) templates)) (first templates))
          (return-counts (remove #(= % (first templates)) templates)))))

(defn member [value list]
  (if (seq list)
    (if (= value (first list))
      list
      (recur value (rest list)))))

(defn sortcar [lists]
  "sorts by the first element."
  (sort (fn [x y] (> (first x) (first x)))  lists))

(defn get-diatonic-note [current-note interval scale]
  "a simple variant of choose-from-scale which uses a diatonic interval as its second arg."
  (cond
   (nil? interval)
   []
   (> interval 0)
   (nth (member current-note scale) interval)
   :else
   (nth (member current-note (reverse scale)) (math/abs interval))))

(defn collect-all
  "collects all of the occurances of each member of its arg."
  [map saved-templates]
  (cond
   (empty? saved-templates)
   []
   (= map (second (first saved-templates)))
   (cons (first saved-templates)
         (collect-all map (rest saved-templates)))
   :else (collect-all map (rest saved-templates))))

(defn find-scale-intervals
  "returns the diatonic intervals between the notes according to the scale."
  [notes scale]
  (cond
   (empty? (rest notes))
   []
   (nil? (second notes))
   (cons nil (find-scale-intervals (rest notes) scale))
   :else
   (cons (let [first-note-test (member (first notes) scale)
               second-note-test (member (second notes) scale)]
           (if (< (first notes) (second notes))
             (count
              (drop-last (count second-note-test) first-note-test))
             (-
              (count
               (drop-last (count first-note-test) second-note-test)))))
         (find-scale-intervals (rest notes) scale))))

(defn get-tessitura
  "gets the tessitura or highest/lowest interval of a note list."
  [cantus-firmus scale]
  (let  [scale-intervals-max (find-scale-intervals (list (first cantus-firmus) (apply max cantus-firmus)) scale)
         scale-intervals-min (find-scale-intervals (list (first cantus-firmus) (apply min cantus-firmus)) scale)
         up (math/abs (first scale-intervals-max))
         down (math/abs (first scale-intervals-min))]
    (if (> up down)
      up
      (- down))))

(defn my-last [list]
  "returns th atom last of the list."
  (let [last-item (last list)]
    (if (seq? last-item)
      (first last-item)
      last-item)))

(defn get-map
  "returns the map part of the template."
  [cantus-firmus scale]
  (let [tessitura (get-tessitura cantus-firmus scale)
        scale-intervals (find-scale-intervals (list (first cantus-firmus) (my-last cantus-firmus)) scale)]
    [tessitura (first scale-intervals)]))

(defn select-new-seed-note [cantus-firmus scale saved-templates]
  "select a logical new seed note."
  (let [map-template (get-map cantus-firmus scale)
        templates (collect-all map-template saved-templates)
        counts (return-counts templates)
        sorted-counts (sortcar counts)
        interval (first (second (first sorted-counts)))]
    (when interval
      (get-diatonic-note (first cantus-firmus) interval scale))))

(defn set-default-goals []
  "sets the default goals for the program."
  (reset! illegal-verticals '(0 1 2 5 6 10 11 13 14 17 18 22 23 25 26 29 30 34 35 -1 -2 -3 -4 -5 -6 -7 -8))
  (reset! illegal-parallel-motions '((7 7)(12 12)(19 19)(24 24)))
  (reset! illegal-double-skips '((3 3)(3 4)(3 -3)(3 -4)(-3 -3)(-3 -4)(-3 3)(-3 4)
                                 (4 3)(4 4)(4 -3)(4 -4)(-4 -3)(-4 -4)(-4 3)(-4 4)))
  (reset! direct-fifths-and-octaves '((9 7)(8 7)(21 19)(20 19))))

(defn get-complement
  "incrementally returns all of the intervals not in the verticals arg."
  ([verticals] (get-complement verticals 0))
  ([verticals number]

     (cond
      (empty? verticals) []
      (member number verticals)
      (get-complement (rest verticals)(+ 1 number))
      :else (cons number (get-complement verticals (+ 1 number))))))

(defn my-sort [function lists]
  "non-destructively sorts its arg by function."
  (sort (fn [x y] (function x y)) lists))

(defn pro [number]
  "projects octaves out from number."
  (if (> number 12)
    (list (- number 12) number (+ number 12))
    (list number (+ number 12)(+ number 24))))

(defn project [numbers]
  ""
  (if (empty? numbers) []
      (concat (pro (first numbers))
              (project (rest numbers)))))

(defn pair [voices]
  "pairs the two lists."
  (if (empty? (first voices))
    []
    (cons (list (first (first voices))(first (second voices)))
          (pair (list (rest (first voices))(rest (second voices)))))))

(defn make-voices [models]
  "makes lists of the cantus firmus and accompanying line pitches."
  (list (apply concat (map first models))(apply concat (map second models))))

(defn get-the-verticals [models]
  "collects the vertical intervals from the models used."
  (my-sort <
           (distinct
            (project
             (let [voiced-music (pair (make-voices models))]
               (map (fn [pair] (- (first pair) (second pair))) voiced-music)
               )))))


(defn get-illegal-verticals [models]
  "returns all of the vertical intervals not in the models."
  (get-complement (get-the-verticals models)))

(defn anyp [find-list target-list]
 "returns any of first arg in second arg."
 (first
  (filter
   (fn [find] (when (member find target-list) find))
   find-list)))

(defn remove-illegal-verticals [illegal-verticals all-verticals]
  "removes the illegal verticals in its second arg."
  (cond
   (empty? all-verticals)
   []
   (anyp illegal-verticals (first all-verticals))
   (remove-illegal-verticals illegal-verticals (rest all-verticals))
   :else
   (cons (first all-verticals)
         (remove-illegal-verticals illegal-verticals (rest all-verticals)))))

(defn find-motions [extent value]
  "sub-function of find-all-possible-motions."
  (if (= 0 value) []
      (cons (list extent value)
            (find-motions extent (- value 1)))))

(defn find-all-possible-motions
  "returns all possible motions to its extent arg."
  ([extend] (find-all-possible-motions extend 0 extend))
  ([extent value save-extent]
     (if (= 0 extent) []
         (concat (find-motions extent save-extent)
                 (find-all-possible-motions (- extent 1) value save-extent)))))

(defn find-the-legals [paired-model]
  "discovers the legal motions in its arg."
  (if (empty? (rest paired-model))
    []
    (cons (list (- (first (first paired-model))(second (first paired-model)))
                (- (first (second paired-model))(second (second paired-model))))
          (find-the-legals (rest paired-model)))))

(defn find-legals [models]
  "collects the legal motions in its arg."
  (if (empty? models)
    []
    (concat (find-the-legals (pair (first models)))
            (find-legals (rest models)))))

(defn remove-legal-motions [legal-motions motions]
  "removes the legal motions from the motions arg."
  (cond
   (empty? legal-motions) motions
   (member (first legal-motions) motions)
   (let [motions (remove (fn [test] (= test (first legal-motions))) motions)
         motions (remove-legal-motions (rest legal-motions) motions)]
     motions)
   :else (remove-legal-motions (rest legal-motions) motions)))

(defn find-illegal-parallels [models]
  "returns the non-used parallels in the models which are assumed to be illegal."
  (let [illegal-verticals (get-illegal-verticals models)
        legal-verticals (remove-illegal-verticals illegal-verticals (find-all-possible-motions 24))
        model-verticals (find-legals models)]
    (remove-legal-motions model-verticals legal-verticals)))

(defn combinations [object list]
  "a sub-function of possible-combinations."
  (map (fn [item] [object item]) list))

(defn possible-combinations
  "returns all possible combinations of its list arg."
  ([list] (possible-combinations list list))
  ([list save-list]
     (if (empty? list)
       []
       (concat (combinations (first list) save-list)
               (possible-combinations (rest list) save-list)))))

(defn set-goals [models]
  "sets the goals for the gradus program."
  (reset! illegal-verticals (get-illegal-verticals models))
  (reset! illegal-parallel-motions (find-illegal-parallels models))
  (reset! direct-fifths-and-octaves (find-illegal-parallels models))
  (reset! illegal-double-skips (possible-combinations '(3 4 -3 -4))))

(defn check-for-nils [choices rules]
  "checking to see if all possible first notes produce rule-conflicting problems."
  (cond
   (empty? choices) true
   (member (list (first choices)
                 nil nil) rules)
   (check-for-nils (rest choices) rules)
   :else nil))

(defn reduce-to-within-octave [interval]
  "reduces diatonic intervals to within the octave."
  (cond
   (and (> (math/abs interval) 7)
        (< interval 0))
   (reduce-to-within-octave (+ interval 7))
   (> (math/abs interval) 7)
   (- interval 7)
   (= 0 interval)
   -7
   :else
   interval))

(defn get-diatonic-interval [interval-class]
  "translates interval-classes into diatonic-interval classes."
  (case interval-class
     1 1
     2 1
     3 2
     4 2
    -1 -1
    -2 -1
    -3 -2
    -4 -2
    :else
    1))

(defn choose-from-scale [current-note interval-class scale]
  "gets the appropriate pitch from the current scale based on the interval class."
  (if (> interval-class 0)
    (nth (member current-note scale) (get-diatonic-interval interval-class))
    (let [interval (math/abs (get-diatonic-interval interval-class))
          notes (member current-note (reverse scale))]
      (nth notes interval))))

(defn create-choices [scale last-choice]
  "creates four possible choices - seconds and thirds - from a previous pitch choice."
  [(choose-from-scale last-choice 1 scale)
   (choose-from-scale last-choice 3 scale)
   (choose-from-scale last-choice -1 scale)
   (choose-from-scale last-choice -3 scale)])

(defn stop-if-all-possibilities-are-nil
  "for stopping if no solution exists."
  [seed-note cantus-firmus rules]
  (check-for-nils
   (map (fn [x]
          (reduce-to-within-octave
           (first (find-scale-intervals (list (first cantus-firmus) x)
                                        major-scale))))
        (create-choices major-scale seed-note)) rules))

(defn consult-rules [rule]
  "calling (consult-rules (-9 (2 -1 -1) (-1 2 -2))) consult-rules returned nil"
  (or (member rule @rules)
      (member rule @temporary-rules)))

(defn the-last [n list]
  "returns the last n of list."
  (take-last n list))

(defn create-interval-rule [rule]
  "creates the interval rule as in (-7 (2 2 2)(-1 1 2))."
  (list (first (find-scale-intervals (list (first (first rule))
                                            (first (second rule)))
                                      major-scale))
        (find-scale-intervals (first rule) major-scale)
        (find-scale-intervals (second rule)  major-scale)))

(defn create-rule [cantus-firmus new-notes]
  "creates rules for the rules variable"
  (let [the-list (the-last 4 new-notes)]
    (create-interval-rule
     (list (the-last (count the-list)
                     (drop-last (- (count cantus-firmus)(count new-notes)) cantus-firmus)) the-list))))

(defn test-for-vertical-dissonance
  "tests to ensure vertical dissonance"
  [cantus-firmus-note choice]
  (if (member (- cantus-firmus-note choice) @illegal-verticals) choice))

(defn firstn [number list]
  "returns the first n of is list arg."
  (take number list))

(defn second-to-last [list]
  "returns the second to last of the list arg."
  (my-last (butlast list)))

(defn test-for-parallel-octaves-and-fifths
  "tests for parallel octaves and fifths."
  [cantus-firmus choice last-notes]
  (let [cantus-firmus-to-here (firstn (+ 1 (count last-notes)) cantus-firmus)]
    (cond
     (or (not (>= (count cantus-firmus-to-here) 2))(not (>= (count last-notes) 1)))
     []
     (member (list (math/abs (- (second-to-last cantus-firmus-to-here)(my-last last-notes)))
                   (math/abs (- (my-last cantus-firmus-to-here) choice)))
             illegal-parallel-motions)
     true
     :else nil)))

(defn third-to-last [list]
  "returns the third to last of the list arg."
  (nth (butlast list) (- (count list) 3)))

(defn opposite-sign [numbers]
  "returns t if the two numbers have opposite signs."
  (if (or (and
           (< (first numbers) 0)
           (> (second numbers)) 0)
          (and
           (> (first numbers) 0)
           (< (second numbers) 0)))
    true))

(defn test-for-leaps [extended-last-notes]
  "tests for leaps and avoids two in row and ensures that leaps are followed by contrary motion steps."
  (cond
   (not (>= (count extended-last-notes) 3))
   []
   (member (list (- (second-to-last extended-last-notes)(my-last extended-last-notes))
                 (- (third-to-last extended-last-notes)(second-to-last extended-last-notes)))
           illegal-double-skips)
   true
   (and (> (math/abs (- (third-to-last extended-last-notes)(second-to-last extended-last-notes))) 2)
        (not (opposite-sign (list (- (second-to-last extended-last-notes)(my-last extended-last-notes))
                                  (- (third-to-last extended-last-notes)(second-to-last extended-last-notes))))))
   true
   :else []))

(defn skipp [notes]
  "returns true if its two-number arg is a skip."
  (if (> (math/abs (- (second notes)(first notes))) 2) true))

 (defn test-for-simultaneous-leaps [cantus-firmus choice last-notes]
   "tests for the presence of simultaneous leaps."
   (let [cantus-firmus-to-here  (firstn (+ 1 (count last-notes)) cantus-firmus)]
     (cond
      (or (not (>= (count cantus-firmus-to-here) 2))(not (>= (count last-notes) 1)))
      []
      (and (skipp (the-last 2 cantus-firmus-to-here))(skipp (the-last 2 (concat last-notes (list choice)))))
      true
      :else [])))

(defn get-verticals [cantus-firmus new-line]
  "returns the intervals between two lines of counterpoint."
  (if (empty? cantus-firmus) []
      (cons (- (first cantus-firmus)(first new-line))
            (get-verticals (rest cantus-firmus)(rest new-line)))))

 (defn test-for-direct-fifths [cantus-firmus choice last-notes]
   "tests for direct fifths between the two lines."
   (let [cantus-firmus-to-here  (firstn (+ 1 (count last-notes)) cantus-firmus)]
     (cond
      (or (not (>= (count cantus-firmus-to-here) 2))(not (>= (count last-notes) 1)))
      []
      (member (get-verticals (the-last 2 cantus-firmus-to-here)(the-last 2 (concat last-notes (list choice))))
              direct-fifths-and-octaves)
      true
      :else [])))

(defn get-intervals [notes]
  "returns a list of intervals one short of its pitch-list arg."
  (if (empty? (rest notes)) []
      (cons (- (second notes)(first notes))
            (get-intervals (rest notes)))))

(defn test-for-consecutive-motions [cantus-firmus choice last-notes]
  "tests to see if there are more than two consecutive save-direction motions."
  (let [cantus-firmus-to-here  (firstn (+ 1 (count last-notes)) cantus-firmus)]
    (cond
     (or (not (> (count cantus-firmus-to-here) 3))(not (> (count last-notes) 2)))
     []
     (let [last-four-cf (the-last 4 cantus-firmus-to-here)
           last-four-newline (the-last 4 (concat last-notes (list choice)))]
       (not (or (opposite-sign (list (first (get-intervals (firstn 2 last-four-cf)))
                                     (first (get-intervals (firstn 2 last-four-newline)))))
                (opposite-sign (list (first (get-intervals (firstn 2 (rest last-four-cf))))
                                     (first (get-intervals (firstn 2 (rest last-four-newline))))))
                (opposite-sign (list (first (get-intervals (the-last 2 last-four-cf)))
                                     (first (get-intervals (the-last 2 last-four-newline))))))))
     true
     :else [])))


(defn evaluate
  "evaluates the various choices for a next note based on the goals and current rules"
  [cantus-firmus choices last-notes]
  (let [choice (first choices)]
    (cond
     (empty? choices) []
     (and (not (consult-rules (create-rule cantus-firmus (concat last-notes (list choice)))))
          (not (test-for-vertical-dissonance (nth cantus-firmus (count last-notes)) choice))
          (not (test-for-parallel-octaves-and-fifths (firstn (+ 1 (count last-notes)) cantus-firmus)
                                                     choice last-notes))
          (not (test-for-leaps (concat last-notes (list choice))))
          (not (test-for-simultaneous-leaps (firstn (+ 1 (count last-notes)) cantus-firmus)
                                            choice last-notes))
          (not (test-for-direct-fifths (firstn (+ 1 (count last-notes)) cantus-firmus)
                                       choice last-notes))
          (not (test-for-consecutive-motions (firstn (+ 1 (count last-notes)) cantus-firmus)
                                             choice last-notes)))
     (cons choice (evaluate cantus-firmus (rest choices) last-notes))
     :else (evaluate cantus-firmus (rest choices) last-notes))))

(defn very-first [list]
  "returns the first of the first of list."
  (first (first list)))

(defn very-second [list]
  "returns the first of the second of list."
  (first (second list)))

(defn match-rule [rule-for-matching rule]
  "matches the freer rule to the rule from rules."
  (cond
   (and (nil? (first (rest rule-for-matching)))(nil? (first (rest rule))))
   true
   (or (and (= (very-first (rest rule-for-matching))(very-first (rest rule)))
            (= (very-second (rest rule-for-matching))(very-second (rest rule))))
       (and (= (very-first (rest rule-for-matching))(very-first (rest rule)))
            (nil? (very-second (rest rule-for-matching)))))
   (match-rule (cons (first rule-for-matching)(map rest (rest rule-for-matching)))
               (cons (first rule)(map rest (rest rule))))
   :else nil))

(defn match-interval-rule [rule-for-matching rule]
  "matches the freer rule to the rule from rules."
  (cond
   (and (nil? (first rule-for-matching)) (nil? (first rule)))
   true
   (or (and (= (very-first rule-for-matching)(very-first rule))
            (= (very-second rule-for-matching)(very-second rule)))
       (and (= (very-first rule-for-matching)(very-first rule))
            (nil? (very-second rule-for-matching))))
   (match-interval-rule (map rest rule-for-matching) (map rest rule))
   :else nil))

(defn match-rules-freely [rule rules]
  "runs the match-rule function through the rules."
  (cond
   (empty? rules)
   []
   (and (= (first rule)(first (first rules)))
        (match-interval-rule (rest rule)(rest (first rules))))
   true
   (and (= (first rule)(first (first rules)))
        (= (count (second rule))(count (second (first rules))))
        (match-rule rule (first rules)))
   true
   :else (match-rules-freely rule (rest rules))))

(defn third [list]
  (list 3))

(defn reduce-rule [rule]
  "reduces the front-end of the look-ahead rule."
  (if (<= (count (second rule)) 3) rule
      (let [amount (- (count (second rule)) 3)]
        (cons (+ (first rule)(- (first (second rule)))(first (third rule)))
              (map (fn [x](nth x amount)) (rest rule))))))

(defn make-freer-rule [amount cf-notes rule]
  "adds the appropriate number of nils to the new line for look-ahead matching."
  (if (= 0 amount) rule
      (make-freer-rule (- amount 1)
                       (rest cf-notes)
                       (list (first rule)
                             (concat (second rule)(list (first cf-notes)))
                             (concat (third rule)(list nil))))))

(defn create-relevant-cf-notes [last-notes cantus-firmus]
  "creates the set of forward reaching cf notes."
  (firstn 2 (nth cantus-firmus (- (count last-notes) 1))))

(defn look-ahead [amount cantus-firmus last-notes rule rules]
  "the top-level function for looking ahead."
  (match-rules-freely
   (reduce-rule (make-freer-rule amount (find-scale-intervals (create-relevant-cf-notes last-notes cantus-firmus) major-scale) rule))
   rules))

(defn look-ahead-for-best-choice [cantus-firmus last-notes correct-choices]
  "looks ahead for the best choice"
  (cond
   (empty? correct-choices)
   []
   (not (look-ahead 1
                    cantus-firmus
                    (concat last-notes (list (first correct-choices)))
                    (create-rule cantus-firmus (concat last-notes (list (first correct-choices))))
                    rules))
   (first correct-choices)
   :else (look-ahead-for-best-choice cantus-firmus last-notes (rest correct-choices))))

(defn evaluate-choices
  "runs the evaluate and look-ahead functions through the various choices."
  [cantus-firmus choices last-notes]
  (let [correct-choices (evaluate cantus-firmus choices last-notes)]
    (if correct-choices
      (reset! *look-ahead* true)
      (reset! *look-ahead* []))
    (if (> (count correct-choices) 0)
      (look-ahead-for-best-choice cantus-firmus last-notes correct-choices)
      (first correct-choices))))

(defn push [data reference]
  (swap! reference concat data))

(defn pushnew [data reference]
  (when-not (contains? data @reference))
  (swap! reference concat data))

(defn print-backtracking []
  "simple printing function to show backtracking."
  (format "~&~a~&~a~&~a~&" "backtracking.....there are now" (count @rules) "rules."))

(defn position [thing list]
  (.indexOf thing list))

(defn translate-into-pitchnames [list-of-midi-note-numbers]
  "used to translate midi note numbers into note names."
  (if (empty? list-of-midi-note-numbers) []
      (cons (nth list-of-notes (position (first list-of-midi-note-numbers) major-scale))
            (translate-into-pitchnames (rest list-of-midi-note-numbers)))))

(defn translate-notes [first-note intervals]
  "translates interval lists into note names for readability."
  (if (empty? intervals)(translate-into-pitchnames (list first-note))
      (let [test (get-diatonic-note first-note (first intervals) major-scale)]
        (concat (translate-into-pitchnames (list first-note))
              (translate-notes test (rest intervals))))))

(defn translate-rule-into-pitches [first-note rule]
  "translates rules into more readable pitch names."
  (list (translate-notes first-note (second rule))
        (translate-notes (get-diatonic-note first-note (first rule) major-scale)(third rule))))


(defn print-working [cantus-firmus last-notes]
  "simple printing function for continuing to compose"
  (format "~&~a~&~a~&" "working....." (list (translate-into-pitchnames cantus-firmus)(translate-into-pitchnames last-notes))))

(defn get-new-starting-point
  "for backtracking - starts 2 earlier or nil"
  [last-notes]
  (cond
   (<= (count last-notes) 1) []
   :else (butlast last-notes 1)))

(defn create-new-line
  "creates a new line with the cantus firmus."
  ([cantus-firmus scale choices last-notes] (create-new-line cantus-firmus scale choices last-notes (count cantus-firmus)))
  ([cantus-firmus scale choices last-notes length]
  (if (stop-if-all-possibilities-are-nil @*seed-note* @*cantus-firmus* @rules)
    (format "~a~&" "i can find no solution for this cantus firmus.")
    (if (<= length 0) new-line
        (let [test (evaluate-choices cantus-firmus choices last-notes)]
          (if (nil? test)
            (do
              (if (nil? @*look-ahead*)
                (pushnew (create-rule cantus-firmus (concat last-notes (list (first choices)))) rules)
                (pushnew (create-rule cantus-firmus (concat last-notes (list (first choices)))) temporary-rules))
              (do (reset! save-rules @rules)
                  (if (not (< (count @rules)(count @save-rules)))
                    (print-backtracking)))
              (let [new-last-notes (get-new-starting-point last-notes)]
                (reset! new-line (drop-last (- (count last-notes)(count new-last-notes)) @new-line))
                (create-new-line cantus-firmus
                                 scale
                                 (remove (my-last last-notes)
                                         (shuffle (create-choices
                                               major-scale
                                               (if (nil? new-last-notes) *seed-note* (my-last new-last-notes)))))
                                 new-last-notes
                                 (+ length (- (count last-notes)(count new-last-notes))))))
            (do (reset! new-line (concat @new-line (list test)))
                (if *print-state* (print-working cantus-firmus @new-line))
                (create-new-line cantus-firmus
                                 scale
                                 (shuffle (create-choices major-scale test))
                                 (concat last-notes (list test))
                                 (- length 1)))))))))

(defn make-event [ontime pitch channel]
  "creates an event based on args."
  (list ontime
        (if (symbol? pitch) (eval pitch) pitch)
        1000
        channel
        90))

(defn make-events
  "makes consecutive events out of the pairs of pitches in its arg."
  ([pitch-groupings] (make-events pitch-groupings 0))
  ([pitch-groupings ontime]
  (if (empty? pitch-groupings) []
      (concat (list (make-event ontime (first (first pitch-groupings)) 1)
                    (make-event ontime (second (first pitch-groupings)) 2))
              (make-events (rest pitch-groupings)(+ ontime 1000))))))

(defn analyze-for-template [seed-note cantus-firmus scale]
  "returns the complete template (seed interval and map) for saving."
  (list (first (find-scale-intervals (list (first cantus-firmus) seed-note) scale))
        (get-map cantus-firmus scale)))

(defn gradus
  "top-level function of the counterpoint program."
  [& [auto-goals print-state seed-note cantus-firmus]]
  (let [auto-goals (or auto-goals @*auto-goals*)
        print-state (or print-state @*print-state*)
        seed-note (or seed-note nil)
        cantus-firmus (or cantus-firmus @*cantus-firmus*)]
    (when-not (= last-cantus-firmus @*cantus-firmus*)
            (do
              (reset! temporary-rules [])
              (reset! last-cantus-firmus @*cantus-firmus*)))

    (if seed-note
      (reset! *seed-note* seed-note)
      (let [test (select-new-seed-note @*cantus-firmus* major-scale @saved-templates)]
        (if test (reset! *seed-note* test))))
    (reset! *auto-goals* auto-goals)
    (reset! *print-state* print-state)
    (reset! *cantus-firmus* cantus-firmus)
    (if (nil? @*auto-goals*)
      (set-default-goals))
    (if @*auto-goals*
      (do (set-goals models)
          (reset! *auto-goals* [])
          (reset! past-model-count (count models))))
    (if (not (= (count models) @past-model-count)) (set-goals models))
    (reset! past-model-count (count models))
    (reset! new-line [])
    (reset! solution
            (create-new-line @*cantus-firmus*  major-scale (shuffle (create-choices major-scale @*seed-note*)) nil))
    (reset! save-voices (list (firstn (count @solution) @*cantus-firmus*)
                              @solution))
    (reset! save-voices (map translate-into-pitchnames @save-voices))
    (reset! counterpoint (make-events (pair @save-voices)))
    (if (= (count @*cantus-firmus*)(count (second @save-voices)))
      (push (analyze-for-template seed-note @*cantus-firmus* major-scale)
            saved-templates))
    counterpoint))

(defn replenish-seed-notes []
  "replenishes the seednotes when when they have all been used."
  (reset! seed-notes '(60 65 64 62 59 57 55 53)))

(defn choose-one [list]
  "chooses one its arg randomly."
  (nth list (rand-int (count list))))

(defn evaluate-pitch-names
  "evaluates the pitch names of its arg into midi note numbers."
  [voices]
  (map (fn [x] (map eval x)) voices))

(defn create-canon
  "creates a simple canon in two voices using gradus."
  []
  (reset! *seed-note* (- (my-last @*cantus-firmus*) 12))
  (gradus)
  (reset! save-voices (evaluate-pitch-names @save-voices))
  (let [theme (concat @*cantus-firmus* (map (fn [x] (+ x 12)) (second @save-voices)))
        lower-voice (map (fn [x](- x 12)) theme)]
    (make-events
     (pair (list (concat theme theme theme (vec  (repeat (count @*cantus-firmus*) 0)))
                 (concat
                  (vec (repeat (count @*cantus-firmus*) 0))
                  lower-voice lower-voice lower-voice))))))

(defn compose []
  (create-canon))
