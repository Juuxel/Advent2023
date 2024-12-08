type Cell =
    | Space
    | Galaxy

module Cell =
    let parse c =
        match c with
        | '.' -> Space
        | '#' -> Galaxy
        | _ ->
            eprintfn "Unknown cell: %O" c
            Space

let findGalaxies (image: Cell[,]) =
    seq {
        for row = 0 to Array2D.length1 image - 1 do
            for col = 0 to Array2D.length2 image - 1 do
                if image[row, col] = Galaxy then
                    yield row, col
    }

let expandGalaxies (image: Cell[,]) (factor: int64) (galaxies: (int * int) list) =
    let initialRowCount = Array2D.length1 image
    let initialColCount = Array2D.length2 image

    let emptyRows =
        seq {
            for i = 0 to initialRowCount - 1 do
                if image[i, *] |> Array.forall ((=) Space) then
                    yield i
        }
        |> Seq.toList

    let emptyCols =
        seq {
            for i = 0 to initialColCount - 1 do
                if image[*, i] |> Array.forall ((=) Space) then
                    yield i
        }
        |> Seq.toList

    galaxies
    |> List.map (fun (row, col) ->
        let rowOffset = emptyRows |> List.filter ((>) row) |> List.length
        let colOffset = emptyCols |> List.filter ((>) col) |> List.length
        int64 row + int64 rowOffset * (factor - 1L), int64 col + int64 colOffset * (factor - 1L))

let distance (x1: int64, y1: int64) (x2: int64, y2: int64) = abs (x1 - x2) + abs (y1 - y2)

let pairs xs =
    seq {
        for i = 0 to List.length xs - 2 do
            for j = i + 1 to List.length xs - 1 do
                yield xs[i], xs[j]
    }

let lines = System.IO.File.ReadLines "day11.txt" |> Seq.toList
let rows = lines.Length
let cols = lines[0].Length
let image = Array2D.init rows cols (fun row col -> lines[row][col] |> Cell.parse)
let galaxies = findGalaxies image |> Seq.toList

let part1 =
    galaxies
    |> expandGalaxies image 2L
    |> pairs
    |> Seq.sumBy (fun (a, b) -> distance a b)

printfn "Part 1: %d" part1

let part2 =
    galaxies
    |> expandGalaxies image 1_000_000L
    |> pairs
    |> Seq.sumBy (fun (a, b) -> distance a b)

printfn "Part 2: %d" part2
