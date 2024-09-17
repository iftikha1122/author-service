package com.document.manager.authors.domain;

import com.document.manager.authors.api.request.CreateAuthorDto;
import com.document.manager.authors.api.request.UpdateAuthorDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
@Table(name = "AUTHORS" ,uniqueConstraints = {
        @UniqueConstraint(columnNames = "USER_NAME")
})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "FIRST_NAME")
    private String firstName;
    @Column(name = "LAST_NAME")
    private String lastName;
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private Status status;
    @Enumerated(EnumType.STRING)
    @Column
    private Role role;
    @Column(name = "USER_NAME")
    private String userName;
    @Column(name = "PASSWORD")
    private String password;


    public static Author from(CreateAuthorDto authorDTO,String password){
        return Author.builder().firstName(authorDTO.firstName())
                .lastName(authorDTO.lastName())
                .status(Status.ACTIVE)
                .userName(authorDTO.userName())
                .role(Role.ROLE_AUTHOR)
                .password(password)
                .build();
    }

    public void updateAuthor(UpdateAuthorDto updateAuthorDto){
        if(!Objects.isNull(updateAuthorDto.firstName()) && !updateAuthorDto.firstName().trim().isEmpty()){
            this.setFirstName(updateAuthorDto.firstName());
        }

        if(!Objects.isNull(updateAuthorDto.lastName()) && !updateAuthorDto.lastName().isEmpty()){
            this.setLastName(updateAuthorDto.lastName());
        }

    }

    public void markDeleted(){
        this.status = Status.INACTIVE;
    }
}
