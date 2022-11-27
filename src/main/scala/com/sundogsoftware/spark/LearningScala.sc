val shipList = List("Enterprise", "Defiant", "Voyager", "Deep Space Nine")

val backShip = shipList.map((ship: String) => {ship.reverse})

val numberList = List(1,2,3,4)
val sum = numberList.reduce((x : Int, y: Int) => x+y)

val hate = numberList.filter((x : Int) => x!= 4)

val hateThree = numberList.filter(_ !=2)