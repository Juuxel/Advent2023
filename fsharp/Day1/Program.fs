let digits =
    [ "one"; "two"; "three"; "four"; "five"; "six"; "seven"; "eight"; "nine" ]
    |> List.indexed

let parseDigits includeWords (line: string) =
    seq {
        for i = 0 to line.Length - 1 do
            let c = line[i]

            if '0' <= c && c <= '9' then
                yield (c - '0') |> int
            elif includeWords then
                for (wordIndex, word) in digits do
                    if line[i .. (i + word.Length - 1)] = word then
                        yield wordIndex + 1
    }
    |> Seq.toList

let lines = System.IO.File.ReadLines("day1.txt") |> Seq.toList

let calculate includeWords =
    lines
    |> List.map (parseDigits includeWords)
    |> List.sumBy (fun digits -> 10 * (List.head digits) + (List.last digits))

printfn "Part 1: %d" (calculate false)
printfn "Part 2: %d" (calculate true)
