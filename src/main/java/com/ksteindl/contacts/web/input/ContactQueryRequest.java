package com.ksteindl.contacts.web.input;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContactQueryRequest {

    public final String sortBy;
    public final String queryString;
    public final Integer page;
    public final Integer size;

    private ContactQueryRequest(String sortBy, String queryString, Integer page, Integer size) {
        this.sortBy = sortBy;
        this.queryString = queryString;
        this.page = page;
        this.size = size;
    }

    public static class ContactQueryRequestBuilder {

        private String sortBy;
        private String queryString;
        private Integer page;
        private Integer size;

        public static ContactQueryRequestBuilder builder() {
            return new ContactQueryRequestBuilder();
        }

        public ContactQueryRequestBuilder setSortBy(String sortBy) {
            this.sortBy = sortBy;
            return this;
        }

        public ContactQueryRequestBuilder setQueryString(String queryString) {
            this.queryString = queryString;
            return this;
        }

        public ContactQueryRequestBuilder setPage(Integer page) {
            this.page = page;
            return this;
        }

        public ContactQueryRequestBuilder setSize(Integer size) {
            this.size = size;
            return this;
        }

        public ContactQueryRequest build() {
            return new ContactQueryRequest(sortBy, queryString, page, size);
        }
    }

}
