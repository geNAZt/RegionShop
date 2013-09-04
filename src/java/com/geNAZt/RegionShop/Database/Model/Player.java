package com.geNAZt.RegionShop.Database.Model;

import com.avaje.ebean.annotation.CacheStrategy;

import javax.persistence.*;
import java.util.List;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 31.08.13
 */

@CacheStrategy(useBeanCache=true, readOnly=false, warmingQuery="order by name")
@Entity()
@Table(name = "Player")
public class Player {
    @Id
    private Integer id;

    private String name;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Region> ownsRegions;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Region> memberInRegions;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Region> getOwnsRegions() {
        return ownsRegions;
    }

    public void setOwnsRegions(List<Region> ownsRegions) {
        this.ownsRegions = ownsRegions;
    }

    public List<Region> getMemberInRegions() {
        return memberInRegions;
    }

    public void setMemberInRegions(List<Region> memberInRegions) {
        this.memberInRegions = memberInRegions;
    }
}
