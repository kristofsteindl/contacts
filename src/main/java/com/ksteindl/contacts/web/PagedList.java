package com.ksteindl.contacts.web;

import com.ksteindl.contacts.domain.entities.Contact;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

@Data
public class PagedList {

    private List<Contact> content;
    private Integer currentPage;
    private Long totalItems;
    private Integer totalPages;

    public PagedList(Page<Contact> pagedList) {
        this.content = pagedList.getContent();
        this.currentPage = pagedList.getNumber();
        this.totalItems = pagedList.getTotalElements();
        this.totalPages = pagedList.getTotalPages();
    }

    @Override
    public String toString() {
        return "PagedList{" +
                "content.size()=" + content.size() +
                "currentPage=" + currentPage +
                ", totalItems=" + totalItems +
                ", totalPages=" + totalPages +
                '}';
    }
}
