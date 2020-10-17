package ru.carSale.model;

import java.util.List;
import java.util.Objects;

public class Filter {
    private boolean sold;
    private int customerId;
    private int firstAdvert;
    private int maxSize;
    private boolean lastDayOnly;
    private boolean withPhotoOnly;
    private int brand_id;

    public boolean isSold() {
        return sold;
    }

    public void setSold(boolean sold) {
        this.sold = sold;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getFirstAdvert() {
        return firstAdvert;
    }

    public void setFirstAdvert(int firstAdvert) {
        this.firstAdvert = firstAdvert;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public boolean isLastDayOnly() {
        return lastDayOnly;
    }

    public void setLastDayOnly(boolean lastDayOnly) {
        this.lastDayOnly = lastDayOnly;
    }

    public boolean isWithPhotoOnly() {
        return withPhotoOnly;
    }

    public void setWithPhotoOnly(boolean withPhotoOnly) {
        this.withPhotoOnly = withPhotoOnly;
    }

    public int getBrand_id() {
        return brand_id;
    }

    public void setBrand_id(int brand_id) {
        this.brand_id = brand_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Filter filter = (Filter) o;
        return sold == filter.sold &&
                customerId == filter.customerId &&
                firstAdvert == filter.firstAdvert &&
                maxSize == filter.maxSize;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sold, customerId, firstAdvert, maxSize);
    }
}
