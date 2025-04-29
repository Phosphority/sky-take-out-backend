package com.sky.service.impl;

import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.exception.DefaultAddressIsOnlyException;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AddressBookServiceImpl implements AddressBookService {

    @Resource
    private AddressBookMapper addressBookMapper;

    @Override
    public void add(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId().get(JwtClaimsConstant.USER_ID));
        addressBookMapper.add(addressBook);
    }

    @Override
    public List<AddressBook> list(Long userId) {
        return addressBookMapper.list(userId);
    }

    @Override
    public void delete(Long id) {
        addressBookMapper.delete(id);
    }

    @Override
    public void updateDefault(Long id) {
        Long userId = BaseContext.getCurrentId().get(JwtClaimsConstant.USER_ID);

        // TODO
//        AddressBook addressBook = addressBookMapper.findDefaultAddress(userId);

//        // 检查地址簿中是否已有默认地址,或者说该地址是否已经是默认地址
//        if(addressBook != null && !addressBook.getId().equals(id)) {
//            throw new DefaultAddressIsOnlyException(MessageConstant.DEFAULT_ADDRESS_IS_ONLY);
//        }else{
//            throw new DefaultAddressIsOnlyException(MessageConstant.ALREADY_DEFAULT_ADDRESS);
//        }
//        // 最后查询到地址簿中的结果为null，再将默认地址设置为该地址
        
    }

    @Override
    public void getDefault() {
        Long userId = BaseContext.getCurrentId().get(JwtClaimsConstant.USER_ID);
        addressBookMapper.getDefault(userId);
    }

    @Override
    public void update(AddressBook addressBook) {
        addressBookMapper.update(addressBook);
    }

    @Override
    public AddressBook findById(Long id) {
        return addressBookMapper.findById(id);
    }
}
