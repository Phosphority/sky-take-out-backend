package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {
    void add(AddressBook addressBook);

    List<AddressBook> list(Long userId);

    void delete(Long userId);

    void updateDefault(Long id);

    void getDefault();

    void update(AddressBook addressBook);

    AddressBook findById(Long id);
}
