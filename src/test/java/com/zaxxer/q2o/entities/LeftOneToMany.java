package com.zaxxer.q2o.entities;

import com.zaxxer.q2o.OneToOneTest;

import javax.persistence.*;
import java.util.Collection;

/**
 * @author Holger Thurow (thurow.h@gmail.com)
 * @since 21.05.18
 */
@Entity
@Table(name = "LEFT_TABLE")
public class LeftOneToMany {
   private int id;
   private String type;
   private Collection<OneToOneTest.Right> rights;

   @Id
   @GeneratedValue
   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   @OneToMany
   @JoinColumn(name = "id", table = "RIGHT_TABLE")
   public Collection<OneToOneTest.Right> getRights() {
      return rights;
   }

   public void setRights(Collection<OneToOneTest.Right> rights) {
      this.rights = rights;
   }

   @Column(name = "type")
   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   @Override
   public String toString() {
      return "LeftOneToMany{" +
         "id=" + id +
         ", type='" + type + '\'' +
         ", rights=" + rights +
         '}';
   }
}
