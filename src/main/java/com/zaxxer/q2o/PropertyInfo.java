package com.zaxxer.q2o;

import javax.persistence.*;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * "It is required that the entity class follow the method signature conventions for JavaBeans read/write properties (as defined by the JavaBeans Introspector class) for persistent properties when property access is used.” (JSR 317: JavaTM Persistence API, Version 2.0, 2.2 Persistent Fields and Properties)
 *
 * @author Holger Thurow (thurow.h@gmail.com)
 * @since 23.04.18
 */
class PropertyInfo extends AttributeInfo {

   private PropertyDescriptor propertyDescriptor;
   private Method readMethod;

   PropertyInfo(Field field, Class clazz) {
      super(field, clazz);
   }

   protected void extractFieldName(final Field field) {
      try {
         propertyDescriptor = new PropertyDescriptor(field.getName(), getOwnerClazz());
         readMethod = propertyDescriptor.getReadMethod();
         name = propertyDescriptor.getName();
      }
      catch (IntrospectionException ignored) {
         // In case of fields with no getters/setters according to JavaBean conventions.
         ignored.printStackTrace();
         toBeConsidered = false;
      }
   }

   @Override
   protected OneToOne extractOneToOneAnnotation() {
      return readMethod.getDeclaredAnnotation(OneToOne.class);
   }

   @Override
   protected ManyToOne extractManyToOneAnnotation() {
      return readMethod.getDeclaredAnnotation(ManyToOne.class);
   }

   @Override
   protected ManyToMany extractManyToManyAnnotation() {
      return readMethod.getDeclaredAnnotation(ManyToMany.class);
   }

   @Override
   protected OneToMany extractOneToManyAnnotation() {
      return readMethod.getDeclaredAnnotation(OneToMany.class);
   }

   @Override
   protected JoinColumns extractJoinColumnsAnnotation() {
      return readMethod.getDeclaredAnnotation(JoinColumns.class);
   }

   @Override
   protected Transient extractTransientAnnotation() {
      return readMethod.getDeclaredAnnotation(Transient.class);
   }

   @Override
   protected JoinColumn extractJoinColumnAnnotation() {
      return readMethod.getDeclaredAnnotation(JoinColumn.class);
   }

   @Override
   protected Enumerated extractEnumeratedAnnotation() {
      return readMethod.getDeclaredAnnotation(Enumerated.class);
   }

   @Override
   protected GeneratedValue extractGeneratedValueAnnotation() {
      return readMethod.getDeclaredAnnotation(GeneratedValue.class);
   }

   @Override
   protected Id extractIdAnnotation() {
      return readMethod.getDeclaredAnnotation(Id.class);
   }

   @Override
   protected Convert extractConvertAnnotation() {
      return readMethod.getDeclaredAnnotation(Convert.class);
   }

   @Override
   protected Column extractColumnAnnotation() {
      return readMethod.getDeclaredAnnotation(Column.class);
   }

   Object getValue(final Object target) throws IllegalAccessException, InvocationTargetException {
      if (!isJoinColumn) {
         return readMethod.invoke(target);
      }
      Object obj = readMethod.invoke(target);
      return idValueFromParentEntity(obj);
   }

   public void setValue(final Object target, final Object value) throws IllegalAccessException {
      try {
         if (!isJoinColumn) {
            propertyDescriptor.getWriteMethod().invoke(target, value);
         }
         else {
            final Object obj;
            if (value.getClass() != getActualType() && !isOneToManyAnnotated) {
               obj = idValueToParentEntity(getType(), value);
            }
            else {
               obj = value;
            }
            propertyDescriptor.getWriteMethod().invoke(target, obj);
         }
      }
      catch (InvocationTargetException | InstantiationException e) {
         e.printStackTrace();
         throw new RuntimeException(e);
      }
   }

   @Override
   boolean isToBeConsidered() {
      return toBeConsidered && !isTransient;
   }

   protected void processJoinColumnAnnotation() {
      try {
         super.processJoinColumnAnnotation();
      }
      catch (Exception ignored) {
         // ignore java.lang.RuntimeException: JoinColumn annotations can only be self-referencing
         toBeConsidered = false;
      }
   }

   @Override
   public String toString() {
      return "PropertyInfo{" +
         "propertyDescriptor=" + propertyDescriptor +
         ", toBeConsidered=" + toBeConsidered +
         ", readMethod=" + readMethod +
         ", ownerClazz=" + getOwnerClazz() +
         ", name='" + name + '\'' +
         ", field=" + field +
         ", type=" + getType() +
         ", isDelimited=" + isDelimited +
         ", updatable=" + updatable +
         ", insertable=" + insertable +
         ", columnName='" + columnName + '\'' +
         ", delimitedTableName='" + delimitedTableName + '\'' +
         ", enumType=" + enumType +
         ", enumConstants=" + enumConstants +
         ", converter=" + converter +
         ", caseSensitiveColumnName='" + caseSensitiveColumnName + '\'' +
         ", isGeneratedId=" + isGeneratedId +
         ", isIdField=" + isIdField +
         ", isJoinColumn=" + isJoinColumn +
         ", isTransient=" + isTransient +
         ", isEnumerated=" + isEnumerated +
         ", isColumnAnnotated=" + isColumnAnnotated +
         ", delimitedName='" + delimitedName + '\'' +
         ", fullyQualifiedDelimitedName='" + fullyQualifiedDelimitedName + '\'' +
         '}';
   }
}
