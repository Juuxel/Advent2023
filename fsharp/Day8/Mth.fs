module Mth.MthModule

[<TailCall>]
let rec gcd a b =
    match b with
    | 0L -> a
    | b -> gcd b (a % b)

let lcm a b = a * (b / gcd a b)
