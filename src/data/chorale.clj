(ns data.chorale
  (:require
   [data.chorale.jsb1 :refer :all]
   [data.chorale.jsb2 :refer :all]
   [data.chorale.jsb3 :refer :all]
   [data.chorale.jsb4 :refer :all]
   [data.chorale.jsb5 :refer :all]
   [data.chorale.jsb6 :refer :all]
   [data.chorale.jsb7 :refer :all]
   [data.chorale.jsb8 :refer :all]
   [data.chorale.jsb9 :refer :all]
   [data.chorale.jsb10 :refer :all]
   [data.chorale.jsb11 :refer :all]
   [data.chorale.jsb12 :refer :all]
   [data.chorale.jsb13 :refer :all]))

(def bach-dbs '(data.chorale.jsb1 data.chorale.jsb2 data.chorale.jsb3 data.chorale.jsb4
                                  data.chorale.jsb5 data.chorale.jsb6 data.chorale.jsb7 data.chorale.jsb8
                                  data.chorale.jsb9 data.chorale.jsb10 data.chorale.jsb11 data.chorale.jsb12
                                  data.chorale.jsb13))

(defn find-db-ns [db-name]
  (first (filter #(resolve (symbol (str % "/" db-name))) bach-dbs)))

(defn find-db [db-name]
  (-> (str (find-db-ns db-name) "/" db-name)
      symbol
      resolve
      var-get))

(def bach-chorales-in-databases
;1
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
