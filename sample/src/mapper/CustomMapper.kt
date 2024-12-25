package mapper

import org.mapper.generator.*

public object CustomMapper {

    public fun mapPersonResponseToPerson(source: PersonResponse): Person = Person(
        id = source.id,
        name = source.name,
    )

    public fun mapGroupResponseToGroup(source: GroupResponse): Group = Group(
        id = source.id.toInt(),
        name = source.name,
        teacherName = source.teacherName,
        maxValue = source.maxValue,
    )

    public fun mapPersonToPersonResponse(source: Person): PersonResponse = PersonResponse(
        id = source.id,
        name = source.name,
    )

    public fun mapGroupToGroupResponse(source: Group): GroupResponse = GroupResponse(
        id = source.id.toLong(),
        name = source.name,
        teacherName = source.teacherName,
        maxValue = source.maxValue,
    )

    public fun mapManagerResponseToManager(source: ManagerResponse): Manager = Manager(
        firstName = source.firstName,
        secondName = source.secondName,
        age = source.age,
    )

    public fun mapCafeResponseToCafe(source: CafeResponse): Cafe = Cafe(
        id = source.id,
        address = source.address,
        code = source.code,
        description = source.description,
        manager = mapManagerResponseToManager(source.manager),
    )
}
