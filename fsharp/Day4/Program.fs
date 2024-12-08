open System.Collections.Generic
open System.Text.RegularExpressions

type Card =
    { Id: int
      Winning: Set<int>
      OurNumbers: Set<int> }

let rec flipResult xs =
    match xs with
    | [] -> Ok []
    | Ok value :: tail ->
        match (flipResult tail) with
        | Ok tail -> Ok (value :: tail)
        | Error err -> Error err
    | Error err :: _ -> Error err

let parseNumberSet (str: string) =
    str.Split(" ")
    |> Array.toSeq
    |> Seq.filter ((<>) "")
    |> Seq.map int
    |> Set.ofSeq

let parseCard line =
    let matchResult = Regex("^Card +([0-9]+): ([0-9 ]+) \\| ([0-9 ]+)$").Match(line)

    if not matchResult.Success then
        Error "Card did not match regex"
    else
        let id = matchResult.Groups[1].Value |> int
        let winning = matchResult.Groups[2].Value |> parseNumberSet
        let ourNumbers = matchResult.Groups[3].Value |> parseNumberSet

        { Id = id
          Winning = winning
          OurNumbers = ourNumbers }
        |> Ok

let countMatching card =
    let matching = Set.intersect card.Winning card.OurNumbers
    matching.Count

let calculatePart1Score card =
    match (countMatching card) with
    | 0 -> 0
    | n -> pown 2 (n - 1)

let countPart2Cards (allCards: Card list) =
    let remaining = Queue<Card>(allCards)
    let mutable n = 0
    while remaining.Count > 0 do
        let card = remaining.Dequeue()
        let matching = countMatching card
        for index = card.Id to card.Id + matching - 1 do
            remaining.Enqueue(allCards[index])
        n <- n + 1
    n

[<TailCall>]
let rec countCards (allCards: Card list) (initial: Card list) acc =
    if initial.IsEmpty then
        acc
    else
        let next =
            initial
            |> List.collect (fun card -> allCards[card.Id .. card.Id - 1 + countMatching card])
        countCards allCards next (acc + initial.Length)

let lines = System.IO.File.ReadLines("day4.txt") |> Seq.toList
let cards = lines |> List.collect (parseCard >> Result.toList)
let part1 = cards |> List.sumBy calculatePart1Score
let part2 = countCards cards cards 0

printfn "Part 1: %d" part1
printfn "Part 2: %d" part2
