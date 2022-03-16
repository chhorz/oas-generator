/**
 *
 * Copyright 2018-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.chhorz.openapi.common.spi.mapping;

import com.github.chhorz.openapi.common.domain.Schema;
import com.github.chhorz.openapi.common.properties.domain.ParserProperties;
import com.github.chhorz.openapi.common.util.LogUtils;

import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.Map;

public interface TypeMirrorMapper {

	void setup(Elements elements, Types types, LogUtils logUtils, ParserProperties parserProperties, List<TypeMirrorMapper> typeMirrorMapper);

	boolean test(TypeMirror typeMirror);

	Map<TypeMirror, Schema> map(TypeMirror typeMirror, Map<TypeMirror, Schema> parsedSchemaMap);

}
