package com.sky.service;

import com.sky.entity.AddressBook;

public interface AddressBookService {
    void add(AddressBook addressBook);

    AddressBook list(Long userId);

    void delete(Long userId);
}
