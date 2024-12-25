package com.example.mapping

import com.example.mapping.`data`.User
import com.example.mapping.`data`.UserResponse

public object GeneratedMapper {
  public fun mapUserResponseToUser(source: UserResponse): User = User(
  id = source.id,
  name = source.name,
  surName = source.surName,
  address = source.address,
  age = source.age,
  description = source.description,
  )
}
