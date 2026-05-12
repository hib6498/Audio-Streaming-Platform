package com.akash.auth_microservice.user;

import java.util.List;

public record UserLookupResult(List<User> users, List<Long> remaining) {}
