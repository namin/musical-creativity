(ns musical-creativity.unit.composers.t-gradus
  (:require
   [midje.sweet :refer :all]
   [musical-creativity.composers.gradus :refer :all]
   [overtone.music.pitch :as music]))

(namespace-state-changes (before :facts (set-default-goals!)))

(defn pitch-as-note-checker [note-name]
  (chatty-checker [pitch]
                  (= pitch (music/note note-name))))

(defchecker pitch-as-note [note-name]
  (pitch-as-note-checker note-name))

(fact "translate rules into pitches"
  (translate-rule-into-pitches (music/note :C3) '(-9 (1 -1 -1) (-1 -2 2))) => '((:C3 :D3 :C3 :B2) (:A1 :G1 :E1 :G1)))

(fact "consult rules"
  (consult-rules '(-9 (2 -1 -1) (-1 -1 -2))) => nil)

(fact "diatonic interval"
  (get-diatonic-interval 3) =>  2)

(fact "get diatonic note"
  (get-diatonic-note (music/note :A3) -5 major-scale) => (music/note :C3))

(fact "find scale intervals"
  (find-scale-intervals (map music/note [:A3 :E4]) major-scale) => [4])

(fact "find the legals"
  (find-the-legals
   '((69 57) (71 55) (72 57) (74 53) (71 55) (72 53) (74 50) (72 52))) =>
   '((12 16) (16 15) (15 21) (21 16) (16 19) (19 24) (24 20)))

(fact "remove illegal verticals"
  (remove-illegal-verticals
   [0 1 2 5 6 10 11 13 14 17 18 22 23 25 26 29 30 34 35 37 38]
   [[24 24] [24 23] [24 22] [24 21] [24 20]]) =>
   [[24 24] [24 21] [24 20]])

(fact "find all possible motions"
  (take 11 (find-all-possible-motions 24))  =>
  [[24 24] [24 23] [24 22] [24 21] [24 20]
   [24 19] [24 18] [24 17] [24 16] [24 15]
   [24 14]])

(fact "pair"
  (pair '((1 2 3) (4 5 6))) => '((1 4) (2 5) (3 6)))

(fact "combinations"
  (combinations 3 '(3 4 -3 -4)) => '((3 3) (3 4) (3 -3) (3 -4)))

(fact "choose-from-scale"
  (choose-from-scale (music/note :C3) -3 major-scale) => (music/note :A2))

(fact "no-solution-exists?"
  (no-solution-exists?
   (music/note :C3)
   default-cantus-firmus
   '((-7 (1 1 2) (-1 -2 1)) (-9 (1 -1 -1) (-1 -2 2)) (-4 (1) (-1))
     (-4 (1 1) (-2 2))))
  => false)

(fact "select-new-seed-note"
  (select-new-seed-note
   (map music/note [:A3 :B3 :C4 :E4 :D4 :C4 :D4 :C4 :B3 :A3])
   major-scale
   '((-5 (4 0)) (-5 (4 0)))) =>  (music/note :C3))

(fact "collect-all"
  (collect-all '(4 0)
               '((-5 (4 0)))) => '((-5 (4 0))))

(fact "create choices"
  (create-choices major-scale 60) => '(62 64 59 57))

(fact "get new starting point"
  (get-new-starting-point '(57 55 53 52 55)) =>
  '(57 55 53 52))

(fact "reduce rule"
  (reduce-rule '(-11 (2 -1 -1 1) (-1 1 -1 nil))) =>
  '(-14 (-1 -1 1) (1 -1 nil)))

(fact "match rules freely"
  (match-rules-freely
   '(-9 (-1 -1 nil) (1 1 nil))
   '((-9 (-1 1 -1) (-1 -2 2)) (-9 (-1 -1 -1) (1 2 -1))
     (-12 (1 -1 -1) (-1 2 2)) (-11 (2 -1 -1) (-1 2 1)) (-4 (1) (2))
     (-4 (1 1) (-2 -1)) (-9 (1 -1 -1) (-1 -2 -1))
     (-7 (1 1 2) (-1 -2 -2)))) => nil)

(future-fact "create new line"
  (reset! new-line [])
  (create-new-line  default-cantus-firmus
                    major-scale
                    (map music/note [:E3 :A2 :D3 :B2])
                    nil) => (map music/note [:A2 :G2 :F2 :G2 :F2 :A2 :G2 :A2 :B2 :D3]))

(fact "check relevant cf notes"
  (create-relevant-cf-notes '(57 55 57 55 53 57 55 57)
                            '(69 71 72 76 74 72 74 72 71 69)) => '(72 71))

(fact "matching interval rule"
  (match-interval-rule
   '((-1 -1 nil) (-1 -1 nil))
   '((-1 1 -1) (-1 -2 2))) => nil)

(fact "look ahead"
  (look-ahead 1 default-cantus-firmus '(62) '(-4 nil nil) '((-9 (-1 1 -1) (-1 -2 2)) (-9 (-1 -1 -1) (1 2 -1))
                                                                       (-12 (1 -1 -1) (-1 2 2)) (-11 (2 -1 -1) (-1 2 1))
                                                                       (-4 (1) (2)) (-4 (1 1) (-2 -1))
                                                                       (-9 (1 -1 -1) (-1 -2 -1)) (-7 (1 1 2) (-1 -2 -2))))
  => truthy)

(fact "evalulate choices"
  (evaluate-choices default-cantus-firmus
                    (map music/note [:F2 :A2 :E2 :B2])
                    (map music/note [:A2 :G2 :A2 :G2 :F2 :A2 :G2])) => (pitch-as-note :A2))

(fact "evaluate"
  (let [note (evaluate default-cantus-firmus
                       (map music/note [:C2 :E2 :B1])
                       (map music/note [:A1 :G2 :A2 :G2 :F2 :E2 :D2]))]
    (count note) => 1
    (first note)) => (pitch-as-note :E2))

(fact "test for parallel octaves and fifths"
  (parallel-octaves-and-fifths? '(69 71 72 76 74 72) 52 '(57 55 53 52 53)) => nil)

(fact "test for leaps"
  (leaps? (map music/note [:A2 :G2 :A2 :G2 :F2 :A2 :F2])) => truthy)

(fact "test for simultaneous leaps"
  (simultaneous-leaps? '(69 71 72 76 74 72 74 72 71 69) 60 '(57 55 57 55 59 57 55 57 59))  => nil)

(fact "test for direct fifths"
  (direct-fifths? '(69 71 72 76 74 72 74 72 71 69) 60 '(57 55 57 55 53 57 55 57 59)) => nil)

(fact "test for consecutive motions"
  (consecutive-motions? '(69 71 72 76 74 72 74 72 71 69) 65 '(57 55 53 55 59 57 59 60 62)) => nil)