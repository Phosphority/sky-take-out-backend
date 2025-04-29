package com.sky.service;

import com.sky.entity.AddressBook;

public interface AddressBookService {
    void add(AddressBook addressBook);

    AddressBook list(Long userId);

    void delete(Long userId);

    void setDefault(Long id);

    void getDefault();

    void update(AddressBook addressBook);
}
