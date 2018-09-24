/*
 * This file is part of AutomaticInventory, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2017-2018 Neolumia
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.neolumia.autoinventory.modules;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

public class ModuleSerializer<T, M extends Module<T>> implements TypeSerializer<T> {

  private final M module;

  public ModuleSerializer(M mode) {
    this.module = checkNotNull(mode);
  }

  @Override
  public T deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
    final String id = value.getString();
    final Optional<T> mode = module.getValue(value.getString());
    if (!mode.isPresent()) {
      throw new ObjectMappingException("Module not registered: " + id);
    }
    return mode.get();
  }

  @Override
  public void serialize(TypeToken<?> type, T obj, ConfigurationNode value) throws ObjectMappingException {
    final Optional<String> id = module.getId(obj);
    if (!id.isPresent()) {
      throw new ObjectMappingException("Mode not registered: " + obj.getClass().getSimpleName());
    }
    value.setValue(id.get());
  }
}
