open System.Text.RegularExpressions

type Direction =
    | Left
    | Right

type Node =
    { Name: string
      Left: string
      Right: string }

module Node =
    let choose direction node =
        match direction with
        | Left -> node.Left
        | Right -> node.Right

    let isStart (name: string) =
        name.EndsWith "A"

    let isEnd (name: string) =
        name.EndsWith "Z"

type Loop =
    { Start: string
      StartIndex: int
      End: string
      Length: int }

let parseDirection c =
    match c with
    | 'L' -> Left
    | 'R' -> Right
    | _ -> raise <| failwith $"Unknown direction: {c}"

let parseNode str =
    let matchResult = Regex("^(...) = \\((...), (...)\\)$").Match(str)
    if not matchResult.Success then
        raise <| failwith $"Could not parse {str}"
    let groups = matchResult.Groups
    { Name = groups[1].Value
      Left = groups[2].Value
      Right = groups[3].Value }

let directionLine :: _ :: nodeLines = System.IO.File.ReadLines "day8.txt" |> Seq.toList
let directions =
    directionLine.ToCharArray()
    |> Array.map parseDirection
    |> Array.toList

let nodes = 
    nodeLines
    |> List.map parseNode
    |> List.map (fun node -> node.Name, node)
    |> Map.ofList

[<TailCall>]
let rec traversePart1 step (directions: Direction list) node (allNodes: Map<string, Node>) =
    let direction = directions[step % (List.length directions)]
    let next = Node.choose direction node
    if next = "ZZZ" then
        step + 1
    else
        traversePart1 (step + 1) directions allNodes[next] allNodes

let detectLoops (allNodes: Map<string, Node>) =
    Map.keys allNodes
    |> Seq.filter Node.isStart
    |> Seq.map (fun start ->
        let mutable currentNode = start
        let mutable step = 0
        while not (Node.isEnd currentNode) do
            let direction = directions[step % (List.length directions)]
            currentNode <- allNodes[currentNode] |> Node.choose direction
            step <- step + 1
        let startIndex = step
        while step = startIndex || not (Node.isEnd currentNode) do
            let direction = directions[step % (List.length directions)]
            currentNode <- allNodes[currentNode] |> Node.choose direction
            step <- step + 1
        let length = step - startIndex
        { Start = start
          StartIndex = startIndex - length
          End = currentNode
          Length = length }
    )
    |> Seq.toList

if nodes.ContainsKey "AAA" then
    let part1 = traversePart1 0 directions nodes["AAA"] nodes
    printfn "Part 1: %d" part1

let part2 =
    detectLoops nodes
    |> List.map (_.Length >> int64)
    |> List.reduce Mth.MthModule.lcm
printfn "Part 2: %d" part2
