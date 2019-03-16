;;; Constants and very basic functions concerning keycaps.

(ns dmote-keycap.data
  (:require [clojure.spec.alpha :as spec]
            [scad-tarmi.core :as tarmi]))

;;;;;;;;;;;;;;;
;; Constants ;;
;;;;;;;;;;;;;;;

(def mount-1u 19.05)

;; Typical 1U keycap width and depth, approximate.
(def key-width-1u 18.25)
(def key-margin (/ (- mount-1u key-width-1u) 2))

;; Switch data here is based on real-world observation, not purely on data
;; sheets. Some components are modelled for printability and may be
;; incompatible with some versions of real switches.
(def switches
  {:alps
    {:travel 3.5
     :stem {:core           {:size {:x 4.5,   :y 2.2,   :z 5}
                             :positive true}}
     :body {:top            {:size {:x 11.4,  :y 10.2,  :z 7.3}}
            :core           {:size {:x 12.35, :y 11.34, :z 5.75}}
            :slider-housing {:size {:x 13.35, :y 5.95,  :z 5.15}}
            :snap           {:size {:x 12,    :y 13.03, :z 4.75}}}}
   :mx
    {:travel 3.6
     :stem {:shell          {:size {:x 7,     :y 5.25,  :z 3.6}
                             :positive true}
            :cross-x        {:size {:x 4,     :y 1.25,  :z 3.6}
                             :positive false}
            :cross-y        {:size {:x 1.1,   :y 4,     :z 3.6}
                             :positive false}}
     :body {:top            {:size {:x 10.2,  :y 11,    :z 6.6}}
            :core           {:size {:x 14.7,  :y 14.7,  :z 1}}
            :base           {:size {:x 15.6,  :y 15.6,  :z 0.7}}}}})


;;;;;;;;;;;;
;; Schema ;;
;;;;;;;;;;;;

(spec/def ::switch-type (set (keys switches)))
(spec/def ::style #{:maquette :minimal})
(spec/def ::unit-size ::tarmi/point-2d)
(spec/def ::top-size
  (spec/tuple (spec/nilable number?) (spec/nilable number?) number?))
(spec/def ::top-rotation ::tarmi/point-3d)
(spec/def ::bowl-radii ::tarmi/point-3d)
(spec/def ::bowl-plate-offset number?)
(spec/def ::max-skirt-length (spec/and number? #(>= % 0)))
(spec/def ::slope number?)
(spec/def ::error-stem-positive number?)
(spec/def ::error-stem-negative number?)
(spec/def ::error-body-positive number?)
(spec/def ::sectioned boolean?)

;; A composite spec for all valid parameters going into the keycap model.
(spec/def ::keycap-parameters
  (spec/keys :opt-un [::switch-type ::style ::unit-size
                      ::top-size ::top-rotation
                      ::bowl-radii ::bowl-plate-offset
                      ::max-skirt-length ::slope
                      ::error-stem-positive ::error-stem-negative
                      ::error-body-positive ::sectioned]))

;;;;;;;;;;;;;;;
;; Functions ;;
;;;;;;;;;;;;;;;

(declare switch-dimension)  ; Internal.

(defn key-length [units] (- (* units mount-1u) (* 2 key-margin)))

(defn switch-parts
  "The keyword names of the parts of a switch body."
  [switch-type]
  (keys (get-in switches [switch-type :body])))

(defn switch-height
  "The total height of a switch’s body over the mounting plate."
  [switch-type]
  (switch-dimension switch-type :z))

(defn switch-footprint
  "The xy footprint of a switch’s body on the mounting plate."
  [switch-type]
  [(switch-dimension switch-type :x)
   (switch-dimension switch-type :y)])

(defn plate-to-stem-end
  "The distance from the switch mount plate to the end of the switch stem.
  This is exposed because dmote-keycap models place z 0 at the end of the
  stem, not on or in the mounting plate."
  [switch-type]
  (+ (switch-height switch-type)
     (get-in switches [switch-type :travel])))

(defn pressed-clearance
  "The height of the skirt of a keycap above the mounting plate, depressed."
  [switch-type skirt-length]
  (- (switch-height switch-type) skirt-length))

(defn resting-clearance
  "The height of the skirt of a keycap above the mounting plate, at rest."
  [switch-type skirt-length]
  (+ (pressed-clearance switch-type)
     (get-in switches [switch-type :travel])))


;;;;;;;;;;;;;;
;; Internal ;;
;;;;;;;;;;;;;;

(defn- switch-dimension
  "A switch’s body over the mounting plate."
  [switch-type dimension]
  {:pre [(spec/valid? ::switch-type switch-type)]
   :post [(number? %)]}
  (apply max (map #(get-in switches [switch-type :body % :size dimension])
                  (switch-parts switch-type))))
