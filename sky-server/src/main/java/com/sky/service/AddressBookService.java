package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;

import java.util.List;

public interface AddressBookService extends IService<AddressBook> {
    void add(AddressBook addressBook);

    List<AddressBook> list(Long userId);

    void delete(Long userId);

    void updateDefault(Long id);

    AddressBook getDefault();

    void update(AddressBook addressBook);

    AddressBook findById(Long id);
}
