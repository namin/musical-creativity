(ns musical-creativity.composers.chorale)


(def *COMPOSER* 'bach)
(def *RULES-STORAGE* ())
(def *MIX-NAMES* ())
(def *MIX* ())
(def *LEXICONS* ())
(def BACH-DOMINANTS-TONICS ())
(def BACH-START-BEATS ())
(def BACH-DOMINANTS ())
(def BACH-TONICS ())
(def BACH-COMPOSE-BEATS ())
(def BACH-RULES ())
(def *END* ())
(def *HISTORY* ())
(def *EVENTS* ())
(def *TONIC* 'major)
(def *EARLY-EXIT?* ())
(def *END* ())
(def *COMPOSE-NUMBER* 0)
(def *HISTORIES* ())
(def *PREVIOUS-BEAT* ())
(def *SAVE-EVENTS* ())
(def *BEAT-SIZE* 1000)

(def bach [bach-compose-beats bach-start-beats bach-rules])
(def *beats* 4)
(def bach-form [?])

(load-file "data/chorale/jsb1.clj")
(load-file "data/chorale/jsb2.clj")
(load-file "data/chorale/jsb3.clj")
(load-file "data/chorale/jsb4.clj")
(load-file "data/chorale/jsb5.clj")
(load-file "data/chorale/jsb6.clj")
(load-file "data/chorale/jsb7.clj")
(load-file "data/chorale/jsb8.clj")
(load-file "data/chorale/jsb9.clj")
(load-file "data/chorale/jsb10.clj")
(load-file "data/chorale/jsb11.clj")
(load-file "data/chorale/jsb12.clj")
(load-file "data/chorale/jsb13.clj")

(defn create-complete-database [db-names &optional (counter 1)]
  "Loads and makes proper objects out of the db-names arg."
  (if (null db-names) t
      (progn (let ((beats (remove-nils (collect-beats (set-to-zero 
                                                       (sortcar #'< (eval (first db-names)))))))
                   (name ())
                   (start t))
               (loop until (null beats)
                     do (setq name (make-name (first db-names) counter))
                     do 
                     
                     (let ((start-notes (get-onset-notes (first beats)))
                           (destination-notes (get-onset-notes (second beats)))
                           (events (first beats)))
                       (set name
                            (make-instance 'beat-it :start-notes start-notes
                                           :destination-notes destination-notes
                                           :events events
                                           :voice-leading (first 
                                                           (my-push (cons (get-rules start-notes destination-notes name)
                                                                          (list name (very-first (sortcar #'< events))))
                                                                    (concat *composer* '- 'rules)))
                                           :speac ())))
                     do (setf counter (1+ counter))
                     do (setf beats (rest beats))
                     do (put-beat-into-lexicon name)
                     do (my-push name (concat *composer* '- 'compose-beats))
                     do (if start (my-push name (concat *composer* '- 'start-beats)))
                     do (setf start nil)))
             (create-complete-database (rest db-names)))))

(defn set-to-zero [events &optional (subtract (very-first events))]
  "Sets the events to zero."
  (if (null events)()
      (cons (cons (- (very-first events) subtract)
                  (rest (first events)))
            (set-to-zero (rest events) subtract))))

(defn thousandp [number]
  "Returns the number under 1000."
  (if (zerop (mod number 1000)) t))

(defn get-rules [start-notes destination-notes name]
  "Gets the intervals between adjacent sets of the two args."
  (setq *rules-storage* ())
  (let ((test (make-lists-equal (list start-notes destination-notes))))
    (get-rules1 (first test)(second test) name)))

(defn get-rules1 [start-notes destination-notes name]
  "Does the grunt work for get-rules."
  (if (or (null (rest start-notes))(null (rest destination-notes)))
    (reverse *rules-storage*)
    (progn
      (setq *rules-storage* (append (reverse (get-rule (- (first destination-notes) (first start-notes)) 
                                                       (first start-notes) start-notes destination-notes name)) *rules-storage*))
      (get-rules1 (rest start-notes) (rest destination-notes) name))))

(defn get-rule [voice start-note start-notes destination-notes name]
  "Gets the rule between first two args."
  (if (or (null (rest start-notes))(null destination-notes))()
      (cons (list (reduce-interval (- (second start-notes) start-note))
                  voice
                  (- (second destination-notes) (second start-notes))
                  name)
            (get-rule voice start-note (rest start-notes)(rest destination-notes) name))))

(defn reduce-interval [interval]
  "Reduces the interval mod 12."
  (cond ((<= (abs interval) 12) interval)
        ((minusp interval)(reduce-interval (+ interval 12)))
        (t (reduce-interval (- interval 12)))))

(defn make-lists-equal [lists]
  "Ensures the two lists are equal in length."
  (cond ((> (length (first lists))(length (second lists)))
         (list (firstn (length (second lists)) (first lists))(second lists)))
        ((> (length (second lists))(length (first lists)))
         (list (first lists)(firstn (length (first lists)) (second lists))))
        (t lists)))

(defn make-lexicon-name [note-numbers &optional (names *mix-names*)]
  "Creates the appropriate lexicon name for the object."
  (cond ((null *mix*)(implode (cons *composer* (cons '- (hyphenate note-numbers)))))
        ((null names)(implode (cons *composer* (cons '- (hyphenate note-numbers)))))
        ((boundp (implode (cons (first names) (cons '- (hyphenate note-numbers)))))
         (implode (cons (first names) (cons '- (hyphenate note-numbers)))))
        (t (make-lexicon-name note-numbers (mix (rest names))))))

(defn make-name [db-name counter]
  "Simple synonym for imploding the dtabase name and number."
  (implode (cons db-name  (list '- counter))))

(defn hyphenate [note-numbers]
  "Hyphenates the numbers in its arg."
  (if (null note-numbers)()
      (append (if (null (rest note-numbers))
                (list (first note-numbers))
                (list (first note-numbers) '-))
              (hyphenate (rest note-numbers)))))

(defn get-onset-notes [events]
  "Gets the onset pitches for its arg."
  (let ((onbeat (very-first events)))
    (loop for event in events
          if (equal (first event) onbeat)
          collect (second event))))

(defn put-beat-into-lexicon [beat-name]
  "Puts the beat arg into the appropriate lexicon."
  (let ((lexicon-name (make-lexicon-name (start-notes (eval beat-name)))))
    (if (and (exist-lexicon lexicon-name)
             (not (member beat-name (beats (eval lexicon-name)) :test #'equal)))
      (setf (beats (eval lexicon-name)) (cons beat-name (beats (eval lexicon-name))))
      (progn (set lexicon-name
                  (make-instance 'lexicon :beats (list beat-name)))
             (pushnew lexicon-name *lexicons*)))))

(defn exist-lexicon [lexicon-name]
  "Sees if the lexicon exists."
  (boundp lexicon-name))

(defn remove-nils [stuff]
  "Removes the nils from the stuff."
  (cond ((null stuff)())
        ((null (first stuff))
         (remove-nils (rest stuff)))
        (t (cons (first stuff)
                 (remove-nils (rest stuff))))))

(defn collect-beats [events]
  "(collect-beats (sortcar #'< b4003))"
  (if (null events)()
      (let* ((test (collect-by-timing (first-place-where-all-together events) events))
             (reduced-test (nthcdr (length test) events)))
        (cons test 
              (collect-beats reduced-test)))))

(defn return-beat [channel-events &optional (start-time (very-first channel-events))]
  "Returns the beat number of the initiating event."
  (cond ((null channel-events) nil)
        ((and (thousandp (very-first channel-events))
              (not (equal start-time (very-first channel-events))))
         (/ (- (very-first channel-events) start-time) 1000))
        (t (return-beat (rest channel-events)
                        start-time))))

(defn collect-by-timing [timing events]
  "Collects the events accoring to timing."
  (cond ((null events)())
        ((<= (+ (first (first events))(fourth (first events))) timing)
         (cons (first events)
               (collect-by-timing timing (rest events))))
        (t (collect-by-timing timing (rest events)))))

(defn first-place-where-all-together [events]
  "This looks ahead to get the first time they end together"
  (let* ((test (plot-timings events))
         
         (channels (get-channel-numbers-from-events events))
         
         (ordered-timings-by-channel (loop for channel in channels 
                                           collect (collect-timings-by-channel test channel))))
    
    (all-together (first ordered-timings-by-channel)
                  (rest ordered-timings-by-channel))))

(defn all-together [channel channels]
  "Returns the appropriate channel timing."
  (cond ((null channel) (second (my-last (my-last channels)))) ;;; here is our remaining problem!!!!!
        ((find-alignment-in-all-channels (second (first channel)) channels))
        (t (all-together (rest channel) channels))))

(defn find-alignment-in-all-channels [point channels]
  "run this on the channels of the channel-point-lists"
  (cond ((null channels) point)
        ((null point) point)
        ((find-alignment point (first channels))
         (find-alignment-in-all-channels point (rest channels)))
        (t ())))

(defn find-alignment [point channel]
  "? (find-alignment 1000 '((4 1000) (4 1000) (4 5000)))
   t this finds the timing point in the channel"
  (cond ((null channel)())
        ((and (thousandp point)
              (assoc point (mapcar #'reverse channel) :test #'equal))
         t)
        (t (find-alignment point (rest channel)))))

(defn get-channel-numbers-from-events [events &optional (channels ())]
  "simply gets the channel numbers from the music"
  (cond ((null events) channels)
        ((not (member (fourth (first events)) channels :test #'equal))
         (get-channel-numbers-from-events (rest events) (cons (fourth (first events)) channels)))
        (t (get-channel-numbers-from-events (rest events) channels))))

(defn collect-timings-by-channel [timings channel]
  "collects the timings of the channel indicated in second arg"
  (cond ((null timings)())
        ((equal (very-first timings) channel)
         (cons (first timings)
               (collect-timings-by-channel (rest timings) channel)))
        (t (collect-timings-by-channel (rest timings) channel))))

(defn plot-timings [events]
  "Plots out the times of each beat."
  (if (null events)()
      (cons (list (fourth (first events))(+ (very-first events)(third (first events))))
            (plot-timings (rest events)))))

(defn create-pitch-class-set [pitches]
  "Sorts and gets a full pc-set."
  (my-sort #'< (remove-duplicates (create-pc-set pitches))))

(defn create-pc-set [pitches]
  "Creates a full PC set."
  (if (null pitches) ()
      (cons (mod (first pitches) 12)
            (create-pc-set (rest pitches)))))

(defn my-push [stuff place-name]
  "A simple synonym for push."
  (set place-name (cons stuff (eval place-name))))

(def bach-chorales-in-databases 
;;;1
'(b206b b306b b408b b507b b606b
b707b b907b b107b b1306b b1605b b1805b b2007b ;b2011b
b2406bs b2506 b2806b b3006b b3206b b3604 b3706 b3907 
b4003 b4006 b4008 b4311 bnotsure

;;;2
b4407b b4606b b4705b b4803b
b4807b b5505b b5708b b6005b b6206b b6402 b6408b ;b6502b 
b6507b b6606b


b6707b 
;b7007b 
b7011b b7305b b7408b 

;;;3
b7706b b7807b b8008b b8107b b8305b b8405b b8506b b8606b b8707
b8807b b10207b b10306b b8906b b9005b b9106b b9209b b9307b b9408b b9606b
b9906b b10406b b10806b b11007b b11106b b11300b b11407b b11606b b11909b
b12006b ;b12206b 

;;;4
b12206b b12506b b12606b b12705b b13306b b13506b b13906b b15105 
b15301b b15305b b15309b b15403b b15408b b15505b b15606b b15705b b15804b
b15206b b16406b 

;;;5
b16506b b16606b b16806n b16907b b17405b b17606b b17807b b18007b b18305b 
b18400b b18707b b18806b b19406b b19412b b19705b b19707b b19701b b22602b 
b22701b b22707b 

;;;6
b22711b b22902b b24403b b24410b b24415b b24417b b24425b b24432b b24434b 
b24440b b2444b b24446b b24454b b24462b b24511b b24515b b24517b b24522b 
b24526b b24527b b24537b b24530b b24812b b24828b b24833b b24846b b24853b 
b24859b b25200b b25300b 

;;;7
b25400b b25500b b25600b b25700b b25800b b25900b b26000b b26100b b26200b 
b26300b b26400b b26500b b26600b b26700b b26800b b26900b b27000b b27100b 
b27200b b27300b b27400b b27500b b27600b b27700b b27800b b27900b b28000b 
b28100b b28300b b28400b 

;;;8
b28500b b28600b b28700b b28800b b28900b b29000b b29100b b29200b b29300b
b29400b b29600b b29700b b29800b b29900b b30000b b30100b b30200b b30300b
b30400b b30500b 

;;;9
b30600b b30700b b30800b b30900b b31000b b31100b b31200b b31300b b31400b
b31500b b31600b b31700b b31800b b31900b b32100b b32200b b32800b b32900b
b3300b b33100b b33200b b33300b b33400b b33500b b33600b b33700b b33800b
b33900b b34000b b34100b 

;;;10
b34200b b34500b b34600b b34700b b34800b b34900b b35000b b35100b b35200b
b35300b b35400b b35500b b35600b b35700b b35800b b35900b b36000b b36100b
b36200b b36300b 

;;;11
b36400b b36500b b36600b b36700b b36800b b36900b b37000b b37100b b37200b
b37300b b37400b b37500b b37600b b37700b b37800n b37900b b38000b b38100b
b38200b b38300b b38400b b38500b b38600b b38700b b38800b b38900b b39000b
b39100b b39200b b39300b 

;;;12
b39400b b39500b b39600b b39700b b39800b b39900b b40000b b40100b b40200b
b40300b b40400b b40500b b40600b b40700b b40800b b40900b b41000b b41100b
b41200b b41300b b41400b b41500b b42600b b41700b b41800b b41900b b42000b 
b42100b b42200b b42300b 

;;;13
b42400b b42500b b42600bb b42700b b42800b b42900b b43000b b43100b b43200b
b43300b b43400b b43500b b43600b 
;b43700b 
b43800b 
))

(create-complete-database bach-chorales-in-databases)

;;;;;
#|? (COMPOSE-BACH)
T|#
;;;;;

(defn compose-bach []
  "The top-level compose function."
  (compose-b)
  (if (or (null *events*)
          (< (let ((it (my-last (sortcar #'< *events*))))
               (+ (first it)(third it)))
             15000)
          (> (let ((it (my-last (sortcar #'< *events*))))
               (+ (first it)(third it)))
             200000)
          (not (wait-for-cadence *events*))
          (check-for-parallel *events*)
          (null *end*))
    (compose-bach) 
    (progn (setq *save-events* *events*)(setq *events* (ensure-necessary-cadences (sortcar #'< *events*)))
           (if (not (check-mt (get-on-beat *events* (very-first *events*))))
             (setq *events* (delay-for-upbeat *events*)))
           (if (and (null *early-exit?*)(equal *composer* 'bach))
             (setq *events* 
                   (cadence-collapse (transpose-to-bach-range *events*)))(setq *events* ()))
           t)))

(defn compose-b [&optional (counter 0)]
  "The real horse for compose-bach."
  (setq *end* ())
  (setq *history* ())
  (setq *events* 
        (let ((current-beat 
               (find-triad-beginning)))
          (if (match-tonic-minor (firstn 4 (events (eval current-beat))))
            (setq *tonic* 'minor)(setq *tonic* 'major))
          (apply #'append 
                 (re-time
                  (append
                   (loop until (or (setq *early-exit?* (null (destination-notes (eval current-beat))))
                                   (if (and (> counter 36)
                                            (if (equal *tonic* 'minor)
                                              (and (> (find-events-duration (events (eval current-beat))) *beat-size*)
                                                   (match-tonic-minor (events (eval current-beat))))
                                              (and (> (find-events-duration (events (eval current-beat))) *beat-size*)
                                                   (match-bach-tonic (events (eval current-beat))))))
                                     (progn (setq *end* t) t)))
                         do (push current-beat *history*)
                         collect (events (eval current-beat))
                         do (setq *previous-beat* current-beat)
                         do (setq current-beat 
                                  (choose-one 
                                   (let ((beat-choices (beats (eval (make-lexicon-name (destination-notes (eval current-beat)))))))
                                     (if (null (rest beat-choices)) beat-choices (my-remove (list *previous-beat* (incf-beat *previous-beat*)) beat-choices)))))
                         do (incf counter))
                   (progn (push current-beat *history*)
                          (list (events 
                                 (eval current-beat)))))))))
  (if (and (null *early-exit?*)(equal *composer* 'bach))
    ;(setq *events* (transpose-to-bach-range *events*))
    *events* (setq *events* ()))
  (setq *history* (reverse *history*))
  (if *end* (push (list (1+ *compose-number*) *history*) *histories*)))

(defn find-triad-beginning []
  "Returns the db with a triad beginning."
  (let* ((test (choose-one (eval (first (eval *composer*)))))
         (on-beat (get-on-beat (events (eval test))(very-first (events (eval test)))))
         (pcs (create-pitch-class-set (get-pitches on-beat))))
      (if (and (triad? on-beat)
               (or (members-all '(0 4 8) pcs)
                   (members-all '(0 4 7) pcs)
                   (members-all '(0 5 8) pcs)
                   (members-all '(2 7 11) pcs))
               (<= (third (first (events (eval test)))) 1000)
               (equal (length (events (eval test))) 4))
        test
        (find-triad-beginning))))

(defn members-all [arrows target]
  "Checks to see if arrows are all in target."
  (cond ((null arrows) t)
        ((member (first arrows) target)
         (members-all (rest arrows) target))
        (t ())))

(defn triad? [events]
  "Checks to see if the events are a triad."
  (let ((pitch-classes (get-smallest-set (create-pitch-class-set (get-pitches events)))))
    (and (equal (length pitch-classes) 3)
         (and (> (- (second pitch-classes)(first pitch-classes)) 2)
              (< (- (second pitch-classes)(first pitch-classes)) 5))
         (and (> (- (third pitch-classes)(second pitch-classes)) 2)
              (< (- (third pitch-classes)(second pitch-classes)) 5)))))

(defn get-smallest-set [set]
  "Returns the set with the smallest outer boundaries."
  (let* ((projected-sets (project set))
         (set-differentials (get-intervals projected-sets)))
    (nth (position (first (my-sort #'< set-differentials)) set-differentials) projected-sets)))

(defn project [set &optional (length (length set))(times 0)]
  "Projects the pc-set through its inversions."
  (if (equal length times)()
      (cons set
            (project (append (rest set)(list (+ 12 (first set)))) length (1+ times)))))

(defn get-intervals [sets]
  "Returns the intervals in the sets."
  (if  (null sets)()
       (cons (abs (apply #'+ (get-interval (first sets))))
             (get-intervals (rest sets)))))

(defn get-interval [set]
  "Returns the intervals between set members."
  (if (null (rest set))
    () (cons (- (second set)(first set))
             (get-interval (rest set)))))

(defn match-tonic-minor [the-events]
  "Matches minor tonics."
  (let ((events (get-last-beat-events (break-into-beats the-events))))
    (and (not (null events))
         (and (all-members (mapcar #'second events) (apply #'append 
                                                           (loop for note in '(60 63 67)
                                                                 collect (project-octaves note))))
              (match-harmony (my-sort #'< (mapcar #'second events)) '(60 63 67))
              (match-harmony (my-sort #'> (mapcar #'second events)) '(60 63 67))))))

(defn break-into-beats [events]
  "Breaks events into beat-sized groupings."
  (sortcar #'< (apply #'append (chop-into-bites (sortcar #'< events)))))

(defn chop-into-bites [events]
  "Chops beats into groupings."
  (cond ((null events)())
        ((and (equal (third (first events)) 1000)
              (thousandp (very-first events)))
         (cons (list (first events))
               (chop-into-bites (rest events))))
        ((> (third (first events)) 1000)
         (cons (chop (first events))
               (chop-into-bites (append (remainder (first events))(rest events)))))
        (t (cons (get-full-beat (get-channel (fourth (first events)) events))
                 (chop-into-bites (append (remainders (get-channel (fourth (first events)) events))
                                          (append (remove-full-beat (get-channel (fourth (first events)) events))
                                                  (get-other-channels (fourth (first events)) events))))))))

(defn chop [event &optional (begin-time (first event))(duration (third event))]
  "Chops beats over 1000 into beat-sized pieces."
  (if (< duration 1000)()
      (cons (append (list begin-time)
                    (list (second event))
                    '(1000)
                    (nthcdr 3 event))
            (chop event (+ begin-time 1000)(- duration 1000)))))
  
(defn remainder [event &optional (begin-time (first event))(duration (third event))]
  "Returns the remainder of the beat."
  (cond ((null event)())
        ((= duration 1000)())
        ((< duration 1000) (list (append (list begin-time)
                                         (list (second event))
                                         (list duration)
                                         (nthcdr 3 event))))
      (t (remainder event (+ begin-time 1000)(- duration 1000)))))

(defn get-full-beat [events &optional (begin-time (very-first events))(duration 0)]
  "Returns one full beat of the music."
  (cond ((null events)())
        ((= (+ duration (third (first events))) 1000)
         (list (first events)))
        ((> (+ duration (third (first events))) 1000)
         (list (append (firstn 2 (first events))
                       (list (- 1000 duration))
                       (nthcdr 3 (first events)))))
        (t (cons (first events)
                 (get-full-beat (rest events)
                                (+ begin-time (third (first events)))
                                (+ (third (first events)) duration))))))
             
(defn remainders [events &optional (begin-time (very-first events))(duration 0)]
  "Returns remainders of beats."
  (cond ((null events)())
        ((= (+ duration (third (first events))) 1000)
         ())
        ((> (+ duration (third (first events))) 1000)
         (list (append (list (+ begin-time (- 1000 duration)))
                       (list (second (first events)))
                       (list (- (third (first events)) (- 1000 duration)))
                       (nthcdr 3 (first events)))))
        (t (remainders (rest events)
                       (+ begin-time (third (first events)))
                       (+ (third (first events)) duration)))))

(defn remove-full-beat [events &optional (begin-time (very-first events))(duration 0)]
  "Removes one full beat from the events arg."
  (cond ((null events)())
        ((>= (+ duration (third (first events))) 1000)
         (rest events))
        (t (remove-full-beat (rest events)
                             (+ begin-time (third (first events)))
                             (+ (third (first events)) duration)))))

(defn get-other-channels [channel-not-to-get events]
  "Returns all but the first arg channeled events."
  (cond ((null events)())
        ((equal (fourth (first events)) channel-not-to-get)
         (get-other-channels channel-not-to-get (rest events)))
        (t (cons (first events)
                 (get-other-channels channel-not-to-get (rest events))))))

(defn get-long-phrases [distances]
  "Returns phrases of greater than 120000 duration."
  (cond ((null (rest distances))())
        ((> (- (second distances)(first distances)) 12000)
         (cons (firstn 2 distances)
               (get-long-phrases (rest distances))))
        (t (get-long-phrases (rest distances)))))

(defn discover-cadences [missing-cadence-locations ordered-events]
  "Makes an appropriate cadence possible."
  (if (null missing-cadence-locations) ordered-events
      (discover-cadences (rest missing-cadence-locations) 
                     (discover-cadence (first missing-cadence-locations) ordered-events))))

(defn discover-cadence [missing-cadence-locations ordered-events]
  "Discovers an appropriate cadence."
  (let* ((relevant-events (get-region (first missing-cadence-locations)(second missing-cadence-locations) ordered-events))
         (places-for-cadence (find-cadence-place relevant-events))
         (best-location-for-new-cadence (if places-for-cadence (find-best-on-time places-for-cadence) nil)))
    (if (null best-location-for-new-cadence) ordered-events
    (sortcar #'< 
             (append (resolve (get-region best-location-for-new-cadence (+ best-location-for-new-cadence 1000) relevant-events))
                     (remove-region best-location-for-new-cadence (+ best-location-for-new-cadence 1000) ordered-events))))))

(defn find-cadence-start-times [ordered-events]
  "Finds the cadence start times."
  (let ((distance-to-cadence (distance-to-cadence ordered-events)))
    (cond ((null ordered-events)())
          ((null distance-to-cadence)
           (find-cadence-start-times (rest ordered-events)))
          (t (cons distance-to-cadence
                   (find-cadence-start-times (clear-to distance-to-cadence ordered-events)))))))

(defn clear-to [distance-to-cadence ordered-events]
  "Clears the events up to the cadence."
  (cond ((null  ordered-events)())
        ((<= (very-first  ordered-events) distance-to-cadence)
         (clear-to distance-to-cadence (rest ordered-events)))
        (t (cons (first ordered-events)
                 (clear-to distance-to-cadence (rest ordered-events))))))

(defn distance-to-cadence [ordered-events]
  "Returns the distance tocadence of the arg."
  (let ((quarter-note-distance (find-1000s ordered-events))
        (half-note-distance (find-2000s ordered-events)))
    (cond ((and (null quarter-note-distance)(null half-note-distance)) ())
          ((null quarter-note-distance) half-note-distance)
          ((null half-note-distance) quarter-note-distance)
          (t (if (> quarter-note-distance half-note-distance) half-note-distance
                 quarter-note-distance)))))

(defn find-1000s [ordered-events &optional (start-time (very-first ordered-events))]
  "Returns the ontime if the ordered events are all duration 1000."
  (cond ((null ordered-events)())
        ((and (let ((channel-1-event (first (get-channel 1 ordered-events))))
                (and (equal (third channel-1-event) 1000)
                     (equal start-time (first channel-1-event))))
              (let ((channel-1-event (first (get-channel 2 ordered-events))))
                (and (equal (third channel-1-event) 1000)
                     (equal start-time (first channel-1-event))))
              (let ((channel-1-event (first (get-channel 3 ordered-events))))
                (and (equal (third channel-1-event) 1000)
                     (equal start-time (first channel-1-event))))
              (let ((channel-1-event (first (get-channel 4 ordered-events))))
                (and (equal (third channel-1-event) 1000)
                     (equal start-time (first channel-1-event)))))
         start-time)
        (t (find-1000s (rest ordered-events)))))

(defn find-2000s [ordered-events &optional (start-time (very-first ordered-events))]
  "Returns events of 2000 duration."
  (cond ((null ordered-events)())
        ((and (let ((channel-1-event (first (get-channel 1 ordered-events))))
                (and (equal (third channel-1-event) 2000)
                     (equal start-time (first channel-1-event))))
              (let ((channel-1-event (first (get-channel 2 ordered-events))))
                (and (equal (third channel-1-event) 2000)
                     (equal start-time (first channel-1-event))))
              (let ((channel-1-event (first (get-channel 3 ordered-events))))
                (and (equal (third channel-1-event) 2000)
                     (equal start-time (first channel-1-event))))
              (let ((channel-1-event (first (get-channel 4 ordered-events))))
                (and (equal (third channel-1-event) 2000)
                     (equal start-time (first channel-1-event)))))
         start-time)
        (t (find-2000s (rest ordered-events)))))

(defn transpose [amt events]
  "Transposes the events according to its first arg."
  (loop for event in events
        collect (if (not (zerop (second event)))
                  (append (list (first event))(list (+ (second event) amt))(nthcdr 2 event))
                  event)))

(defn get-beat-length [events]
  "this is used in re-time for setting the new time!
   requires that the first in events be sorted to current time!"
  (let ((time (very-first events)))
    (first (my-sort #'> (loop for event in events
                              collect (get-note-timing event time))))))

(defn get-note-timing [event time]
  "grunt work for get-beat-length"
  (- (+ (first event)(third event)) time))

(defn match-them [chord full-chord allowance]
  "Matches the chord with the list of pitches within the allowance."
  (cond ((null chord) t)
        ((and (not (member (first chord) full-chord))
              (zerop allowance))
         ())
        ((not (member (first chord) full-chord))
         (match-them (rest chord) full-chord (1- allowance)))
        (t (match-them (rest chord) full-chord allowance))))

(defn remove-region [begin-time end-time events]
  "Removes the region boardered by begin and end times."
  (cond ((null events)())
        ((and (>= (very-first events) begin-time)
              (< (very-first events) end-time))
         (remove-region begin-time end-time (rest events)))
        (t (cons (first events)
                 (remove-region begin-time end-time (rest events))))))

(defn get-region [begin-time end-time events]
  "Returns the region boardered by begin and end times."
  (cond ((null events)())
        ((and (>= (very-first events) begin-time)
              (< (very-first events) end-time))
         (cons (first events)
               (get-region begin-time end-time (rest events))))
        (t (get-region begin-time end-time (rest events)))))

(defn resolve [beat &optional (on-time (very-first beat))]
  "Resolves the beat if necessary."
  (cond ((null beat)())
        ((equal (third (first beat)) 1000)
         (cons (first beat)
               (resolve (rest beat) on-time)))
        (t (let ((test (get-on-beat (get-channel (fourth (first beat)) beat) on-time)))
             (cons (if (>= (third (first test)) 1000)(first test)
                       (append (firstn 2 (first test)) '(1000) (nthcdr 3 (first test))))
                   (resolve (remove-all (get-channel (fourth (first beat)) beat) beat) on-time))))))

(defn find-best-on-time [on-times]
  "Finds the best ontime."
  (find-closest (+ (/ (- (my-last on-times)(first on-times)) 2)(first on-times)) on-times))

(defn find-cadence-place [ordered-events]
  "Returns the best place for a first cadence."
  (let ((beats (collect-beats ordered-events)))
    (loop for beat in beats
          if (and (on-beat (firstn 4 beat)(very-first beat))
                  (triad? (firstn 4 beat))
                  (not-beyond-1000 beat))
          collect (very-first beat))))

(defn not-beyond-1000 [beat &optional (channel 1)]
  "Returns t if the beat does not contain events beyond the incept time."
  (cond ((equal channel 5) t)
        ((not-beyond (get-channel channel beat))
         (not-beyond-1000 beat (+ channel 1)))
        (t ())))

(defn not-beyond [channel-events]
  "Returns events beyond the initial ontime."
  (if (not (> (apply #'+ (mapcar #'third channel-events)) 1000)) t))

(defn on-beat [events ontime]
  "Returns t if the events conform to ontime."
  (cond ((null events) t)
        ((and (thousandp (very-first events))(equal (very-first events) ontime))
         (on-beat (rest events) ontime))
        (t ())))

(defn find-closest [number list]
  "finds the closest number in list to number."
  (let ((test (loop for item in list
                    collect (abs (- number item)))))
    (nth (choose-one (positions (first (my-sort '< test)) test)) list)))

(defn positions [number list]
  "Shows the positions of number in list."
  (let ((position ()))
    (loop until (null (member number list))
          do (setf position (position number list))
          collect position
          do (setf list  (substitute 'x number list 
                                     :end (1+ position))))))

(defn remove-all [stuff other-stuff]
  "Removes all of the stuff from the other-stuff."
  (loop when (null stuff) return other-stuff
        do (setf other-stuff (remove (first stuff) other-stuff :test 'equal))
        do (setf stuff (rest stuff))))

(defn get-channel [n music]
  "Gets the nth channel of the music."
  (cond ((null music)())
        ((equal (fourth (first music)) n)
         (cons (first music)(get-channel n (cdr music))))
        (t (get-channel n (cdr music)))))

(defn match-harmony [one two]
  "Checks to see if its args match mod-12."
  (match-them (my-sort #'< one)
              (apply #'append 
                     (loop for note in two
                           collect (project-octaves note)))
              (floor (/ (length one) 4))))

(defn project-octaves [note]
  "Projects its arg through a series of octaves."
  (let ((base-note (reduce-it note 20)))
    (loop until (> base-note 120)
          collect (setf base-note (+ base-note 12)))))

(defn reduce-it [note base]
  "Reduces its first arg mod12 below its second arg."
  (if (< note base) note (reduce-it (- note 12) base)))

(defn all-members [list target]
  "Checks to see if its first arg members are present in second arg."
  (cond ((null list) t)
        ((not (member (first list) target)) ())
        (t (all-members (rest list) target))))

(defn get-last-beat-events [events]
  "As its name suggests."
  (let* ((begin-time (first (my-last (sortcar #'< events))))
         (last-beat (get-all-events-with-start-time-of begin-time events)))
    (if (and (equal (length last-beat) 4)
             (thousandp (third (first last-beat))))
      last-beat)))

(defn get-all-events-with-start-time-of [start-time events]
  "As its name suggests."
  (cond ((null events)())
        ((equal (very-first events) start-time)
         (cons (first events)
               (get-all-events-with-start-time-of start-time (rest events))))
        (t (get-all-events-with-start-time-of start-time (rest events)))))

(defn get-pitches [events]
  "Gets the pitches from its arg."
  (loop for event in events
        collect (second event)))

(defn incf-beat [beat]
  "Increments the beat number."
  (implode (list (get-db-name beat) '- (1+ (my-last (explode beat))))))

(defn get-db-name [lexicon]
  "Returns the database name."
  (implode (get-db-n (explode lexicon))))

(defn get-db-n [exploded-lexicon]
  "Checks for dashes in the db-name."
  (cond ((equal (first exploded-lexicon) '-)
         ())
        ((null exploded-lexicon)())
        (t (cons (first exploded-lexicon)
                 (get-db-n (rest exploded-lexicon))))))

(defn match-bach-tonic [the-events]
  "Returns true if the events are tonic."
  (let ((events (get-last-beat-events (break-into-beats the-events))))
    (and (not (null events))
         (and (all-members (mapcar #'second events) (apply #'append 
                                                           (loop for note in '(60 64 67)
                                                                 collect (project-octaves note))))(match-harmony (my-sort #'< (mapcar #'second events)) '(60 64 67))
              (match-harmony (my-sort #'> (mapcar #'second events)) '(60 64 67))))))

(defn find-events-duration [events &optional (duration 0)]
  "Returns the events duration."
  (cond ((null events) duration)
        ((equal (fourth (first events)) 1)
         (find-events-duration (rest events) (+ duration (third (first events)))))
        (t (find-events-duration (rest events) duration))))

(defn re-time [event-lists &optional (current-time 0)]
  "Re-times the beats to fit together."
  (if (null event-lists)()
      (cons (loop for event in (set-to-zero (first event-lists))
                  collect (cons (+ (first event) current-time) (rest event)))
            (re-time (rest event-lists) (+ current-time 
                                           (get-beat-length 
                                            (first event-lists)))))))

(defn transpose-to-bach-range [events]
  "As its name suggests."
  (let* ((high-low (highest-lowest-notes events))
         (intervals-off (list (- 83 (first high-low))
                              (- 40 (second high-low))))
         (middle (put-it-in-the-middle intervals-off)))
    (progn 
           (transpose middle events))))

(defn put-it-in-the-middle [extremes]
  "Gets the average."
  (round (/ (+ (second extremes)(first extremes)) 2)))

(defn highest-lowest-notes [events]
  "Returns the highest and lowest pitches of its arg."
  (list (first (my-sort #'> (loop for event in (get-channel 1 events)
                                  collect (second event))))
        (first (my-sort #'< (loop for event in (let ((test (get-channel 4 events)))
                                                 (if (null test)(get-channel 2 events) test))
                                  collect (second event))))))

(defn cadence-collapse [events]
  "Ensures the final chord will not have offbeats."
  (apply #'append (collapse (collect-beats (sortcar #'< events)))))

(defn collapse [beats]
  "Collapses the final cadence."
  (cond ((null beats)())
        ((and (equal (length (first beats)) 4)
              (equal (third (first (first beats))) 2000))
         (cons (make-1000s (first beats))
               (collapse (reset (rest beats) 1000))))
        (t (cons (first beats)
                 (collapse (rest beats))))))

(defn make-1000s [beat]
  "Makes all of the beat's durations into 1000s."
  (loop for event in beat
        collect (append (firstn 2 event) '(1000) (nthcdr 3 event))))

(defn reset [beats subtraction]
  "Resets the beats appropriately."
  (if (null beats)()
      (cons (loop for event in (first beats)
                  collect (append (list (- (first event) subtraction)) (cdr event)))
            (reset (rest beats) subtraction))))

(defn delay-for-upbeat [events])
  "Delays the upbeat."
  (reset-events-to events 3000))

(defn reset-events-to [events begin-time]
  "Resets the events for the delayed beat."
  (loop for event in (set-to-zero  events)
        collect (cons (+ begin-time (first event))(rest event))))

(defn get-on-beat [events ontime]
  "Returns the on beat from the events."
  (cond ((null events) ()) 
        ((and (thousandp (very-first events))(equal (very-first events) ontime))
         (cons (first events)
               (get-on-beat (rest events) ontime)))
        (t ())))

(defn check-mt [events]
  "Returns the major tonic."
  (get-tonic events))

(defn get-tonic  [events]
  "Returns the tonic."
  (and (or (all (create-pitch-class-set (get-pitches events))
                '(0 4 7))
           (all (create-pitch-class-set (get-pitches events))
                '(0 3 7)))
       (zerop (first (create-pitch-class-set (get-pitches (get-channel 4 (sortcar #'< events))))))))

(defn all [first second]
  "Tests for presence of all of first arg in second arg."
  (cond ((null first) t)
        ((member (first first) second) 
         (all (rest first) second))
        (t ())))


(defn ensure-necessary-cadences [ordered-events]
  "Ensures the cadences are proper."
  (let ((cadence-start-times (find-cadence-start-times ordered-events)))
    (Discover-cadences (get-long-phrases (if (not (zerop (first cadence-start-times))) (cons 0 cadence-start-times) cadence-start-times))
                   ordered-events)))

(defn check-for-parallel [events]
  "Checks for parallel motion."
  (let ((sorted-pitches-by-beat (loop for beat in (collect-beats (firstn 30 (sortcar #'< events)))
                                      collect (get-pitches (get-on-beat beat (very-first beat))))))
    (and (equal (length (first sorted-pitches-by-beat)) 4)
         (equal (length (second sorted-pitches-by-beat)) 4)
         (or (and (plusp (- (first (first sorted-pitches-by-beat))
                            (first (second sorted-pitches-by-beat))))
                  (plusp (- (second (first sorted-pitches-by-beat))
                            (second (second sorted-pitches-by-beat))))
                  (plusp (- (third (first sorted-pitches-by-beat))
                            (third (second sorted-pitches-by-beat))))
                  (plusp (- (fourth (first sorted-pitches-by-beat))
                            (fourth (second sorted-pitches-by-beat)))))
             (and (minusp (- (first (first sorted-pitches-by-beat))
                             (first (second sorted-pitches-by-beat))))
                  (minusp (- (second (first sorted-pitches-by-beat))
                             (second (second sorted-pitches-by-beat))))
                  (minusp (- (third (first sorted-pitches-by-beat))
                             (third (second sorted-pitches-by-beat))))
                  (minusp (- (fourth (first sorted-pitches-by-beat))
                             (fourth (second sorted-pitches-by-beat)))))))))

(defn wait-for-cadence [events &optional (start-time (very-first events))]
  "Ensures the cadence is the proper length."
  (cond ((null events)())
        ((> (very-first events) (+ start-time 4000))
         t)
        ((> (third (first events)) 1000) ())
        (t (wait-for-cadence (rest events) start-time))))