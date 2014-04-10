(ns its.access)

(def timestamp first)
(def level second)
(def message (comp second rest))
(def args (comp vec rest rest rest))

(defn is-level [lvl line]
  (= lvl (level line)))

(def debug? (partial is-level :debug))
(def info? (partial is-level :info))
(def warn? (partial is-level :warn))
(def error? (partial is-level :error))

(def debugs (partial filter debug?))
(def infos (partial filter info?))
(def warns (partial filter warn?))
(def errors (partial filter error?))
