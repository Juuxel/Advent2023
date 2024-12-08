open System.Text.RegularExpressions

type Vector2 =
    { X: float
      Y: float }

    static member (*)(a, v) = { X = a * v.X; Y = a * v.Y }
    static member (+)(v, w) = { X = v.X + w.X; Y = v.Y + w.Y }

type Segment2 = { Start: Vector2; End: Vector2 }

let dx segment = segment.End.X - segment.Start.X
let dy segment = segment.End.Y - segment.Start.Y

let intersect a b =
    let dxA = dx a
    let dxB = dx b
    let dyA = dy a
    let dyB = dy b
    let dxAb = b.Start.X - a.Start.X
    let dyAb = b.Start.Y - a.Start.Y
    let denom = (dxA * dyAb - dyA * dxAb)
    let t = dxAb * dyB - dyAb * dxB
    let u = dyA * dxAb - dxA * dyAb

    if t >= 0 && t <= denom && u >= 0 && u <= denom then
        let ts = t / denom

        Some
            { X = a.Start.X + ts * dxA
              Y = a.Start.Y + ts * dyA }
    else
        None

let isWithinBox boxStart boxEnd point =
    boxStart.X <= point.X
    && boxStart.Y <= point.Y
    && point.X <= boxEnd.X
    && point.Y <= boxEnd.Y

let segmentInBox point direction boxStart boxEnd =
    let tStartX = (boxStart.X - point.X) / direction.X
    let tStartY = (boxStart.Y - point.Y) / direction.Y
    let tEndX = (boxEnd.X - point.X) / direction.X
    let tEndY = (boxEnd.Y - point.Y) / direction.Y

    if tEndX >= 0 && tEndY >= 0 then
        let pStartX = point + tStartX * direction
        let pStartY = point + tStartY * direction
        let pStartXInBox = isWithinBox boxStart boxEnd pStartX
        let pStartYInBox = isWithinBox boxStart boxEnd pStartY

        let pStartOpt =
            match pStartXInBox, pStartYInBox with
            | true, true -> Some <| if tStartX < tStartY then pStartX else pStartY
            | true, false -> Some pStartX
            | false, true -> Some pStartY
            | false, false -> None

        match pStartOpt with
        | Some pStart ->
            let pEndX = point + tEndX * direction
            let pEndY = point + tEndY * direction
            let pEndXInBox = isWithinBox boxStart boxEnd pEndX
            let pEndYInBox = isWithinBox boxStart boxEnd pEndY

            let pEndOpt =
                match pEndXInBox, pEndYInBox with
                | true, true -> Some <| if tEndX < tEndY then pEndX else pEndY
                | true, false -> Some pEndX
                | false, true -> Some pEndY
                | false, false -> None

            match pEndOpt with
            | Some pEnd -> Some { Start = pStart; End = pEnd }
            | None -> None
        | None -> None
    else
        None

let pairs xs =
    seq {
        for i = 0 to List.length xs - 2 do
            for j = i + 1 to List.length xs - 1 do
                yield xs[i], xs[j]
    }

let parseLine line =
    let regex =
        Regex("^([0-9]+), +([0-9]+), +([0-9]+) *@ *([0-9]+), +([0-9]+), +([0-9]+)$")

    let matchResult = regex.Match(line)

    if matchResult.Success then
        let p =
            int64 matchResult.Groups[1].Value, int64 matchResult.Groups[2].Value, int64 matchResult.Groups[3].Value

        let d =
            int64 matchResult.Groups[4].Value, int64 matchResult.Groups[5].Value, int64 matchResult.Groups[6].Value

        Some(p, d)
    else
        None

let testAreaStart = 7.
let testAreaEnd = 27.
let boxStart = { X = testAreaStart; Y = testAreaStart }
let boxEnd = { X = testAreaEnd; Y = testAreaEnd }

let tuples =
    System.IO.File.ReadLines "day24.txt"
    |> Seq.map parseLine
    |> Seq.toList
    |> List.collect Option.toList

let segments =
    tuples
    |> List.collect (fun ((px, py, _), (vx, vy, _)) ->
        let point = { X = float px; Y = float py }
        let direction = { X = float vx; Y = float vy }
        segmentInBox point direction boxStart boxEnd |> Option.toList)

let part1 =
    pairs segments
    |> Seq.map (fun (a, b) ->
        intersect a b
        |> Option.filter (isWithinBox boxStart boxEnd)
        |> Option.isSome)
    |> Seq.sumBy (fun b -> if b then 1 else 0)

printfn "Part 1: %d" part1
