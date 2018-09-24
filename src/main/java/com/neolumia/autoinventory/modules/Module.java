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

import com.neolumia.autoinventory.AutoPlugin;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

public abstract class Module<T> extends AbstractModule {

  private final Map<String, T> container = new HashMap<>();

  protected Module(AutoPlugin plugin) {
    super(plugin);
  }

  public void register(String id, T value) {
    container.put(id.toLowerCase(Locale.ENGLISH), value);
  }

  public void unregister(String id) {
    container.remove(id.toLowerCase(Locale.ENGLISH));
  }

  public Optional<String> getId(T module) {
    return container.entrySet().stream().filter(e -> e.getValue().equals(module)).map(Entry::getKey).findAny();
  }

  public Optional<T> getValue(String id) {
    return Optional.ofNullable(container.get(id.toLowerCase(Locale.ENGLISH)));
  }
}
