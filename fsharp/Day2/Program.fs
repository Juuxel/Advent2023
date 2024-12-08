open System.Text.RegularExpressions

type Round = { Red: int; Green: int; Blue: int }
type Game = { Id: int; Rounds: Round list }

let parseGame line =
    let matchResult = Regex.Match(line, "^Game (.+): (.+)$")

    if not matchResult.Success then
        Error("Could not parse game " + line)
    else
        let groups = matchResult.Groups
        let gameId = groups[1].Value |> int

        let rounds =
            groups[2].Value.Split("; ")
            |> Array.toList
            |> List.map (fun str ->
                let mutable red = 0
                let mutable green = 0
                let mutable blue = 0

                for cube in str.Split(", ") do
                    let split = cube.Split(" ")
                    let value = int split[0]

                    match split[1] with
                    | "red" -> red <- value
                    | "green" -> green <- value
                    | "blue" -> blue <- value
                    | _ -> eprintfn "Unknown key %s in round %s" split[1] str

                { Red = red
                  Green = green
                  Blue = blue })

        Ok { Id = gameId; Rounds = rounds }

let isRoundValid round =
    round.Red <= 12 && round.Green <= 13 && round.Blue <= 14

let isGameValid game = game.Rounds |> List.forall isRoundValid

let maxOf fn game = game.Rounds |> List.map fn |> List.max

let power game =
    let maxRed = game |> maxOf _.Red
    let maxGreen = game |> maxOf _.Green
    let maxBlue = game |> maxOf _.Blue
    maxRed * maxGreen * maxBlue

let games =
    System.IO.File.ReadLines("day2.txt")
    |> Seq.map parseGame
    |> Seq.collect Result.toList
    |> Seq.toList

let idSum = games |> List.filter isGameValid |> List.sumBy (fun game -> game.Id)
let powerSum = games |> List.sumBy power

printfn "%d" idSum
printfn "%d" powerSum
