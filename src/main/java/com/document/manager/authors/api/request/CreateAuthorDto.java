package com.document.manager.authors.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAuthorDto(@NotBlank(message = "First name is required")
                                      @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
                                      String firstName,

                              @NotBlank(message = "Last name is required")
                                      @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
                                      String lastName,

                              @NotBlank(message = "Username is required")
                                      @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
                                      String userName,
                              @NotBlank(message = "Password is required")
                                    @Size(min = 8, max = 50, message = "Password must be between 8 and 20 characters")
                                    String password
                              ) { }
