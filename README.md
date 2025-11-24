<a href="https://jetc.dev/issues/266.html"><img src="https://github.com/user-attachments/assets/5f22db99-e836-451a-92dd-24a06a750c57"></a>

<div align="center">
  <img src="https://github.com/user-attachments/assets/68792aec-916f-44ab-89a6-38296fb1fa68"></img>
  <h1>Jetpack Compose AppBar</h1>
  <table>
    <thead>
      <tr>
        <th>Version</th>
        <th>v1.0.1-beta1</th>
      </tr>
    </tbody>
  </table>
</div>

This package allows easy and light implementation of appbar in jetpack compose environment.

> Designed to implement flexible appbar behavior by utilizing only fundamental underlying behaviors and excluding unnecessary features.

## How to make it dependent?
This example for Gradle about Groovy & Kotlin DSL.

```kts
implementation("dev.ttangkong:compose_appbar:1.0.1-beta1")
```

## How to make appbar?
Please refer to the code below!

> See also, this code is the simplest example in this package.

```kotlin
AppBarConnection(
  AppBar(behavior = MaterialAppBarBehavior()) {
    // ... child
  }
  SizedAppBar(minExtent = 50, maxExtent = 100) {
    // ... child
  }
) {
  // Only scrollable component of vertical direction are available.
  // And, can wrapping to parent.
  LazyColumn {
    // ... items
  }
}
```

### How to apply effects according to appbar position?
Please refer to the code below!

> See also, this code is an example of implementing transparency effect.

#### A good example
This example is one of the ideal and simple or basic examples.

```kotlin
// Based on oneself.
AppBarConnection {
  AppBar {
    Box(modifier = Modifier.graphicsLayer {
      alpha = it.expandedPercent()
    }) {
      // ... child
    }
  }
}

// Based on others.
// See also, the second appbar depends on the state of the first appbar.
AppBarConnection {
  val first = rememberAppBarState()

  AppBar(state = first) {
    // ... skip
  }
  AppBar {
    Box(modifier = Modifier.graphicsLayer {
      alpha = first.shrinkedPercent()
    }) {
      // ... child
    }
  }
}
```

#### A bad example
This example is also one of the bad development habits that causes performance degradation.

```kotlin
AppBarConnection {
  AppBar {
    // This reduces performance, because re-composition when scrolled appbar.
    Box(modifier = Modifier.alpha(it.expandedPercent())) {
      // ... child
    }
  }
}
```

## How to customize appbar alignment?
Try applying the `AppBarAlignment` that is a providing standard enumeration in this package.

> This alignment constants for only the hide-able appbar.

```kotlin
AppBarConnection(
  AppBar(alignment = AppBarAlignment.Center) {}
)
```

### properties of AppBarAlignment
| Properie | Description
| ------ | ------ |
| Scroll | Display the same as the scroll item. (is Default Value)
| Center | Based on the size of the appbar, the center is located at the center of the size of the appbar.
| Absolute | Even if the appbar is reduced and expanded, the absolute position of the appbar does not change.

## How to customize appbar?
Try using the `MaterialAppBarBehavior` that is a providing standard feature in this package.

```kotlin
AppBarConnection {
  AppBar(behavior = MaterialAppBarBehavior(floating = false, dragOnlyExpanding = true)) {}
}
```

### properties of MaterialAppBarBehavior
> When the value of floating is true, the value of dragOnlyExpanding must be false.

| Properie | Description | Default value | Type
| ------ | ------ | ------ | ------
| floating | Whether possible with the app bar expands or shrinks even if a user do not scroll to the highest upper (when scroll offset 0). | true | Boolean
| dragOnlyExpanding | Whether only possible with drag when the user needs to start extending the appbar. | false | Boolean
| alignment | Enables automatic alignment of the app bar after scrolling ends, snapping it to fully expanded or fully collapsed state. | true | Boolean
| alignmentDuration | The duration of the automatic alignment animation in milliseconds. | 500 | Int
| alignmentEasing | The easing curve applied to the automatic alignment animation. | Ease | Easing
