import type {LayoutProvider, MediaQueryProvider} from "/@/injection/types.ts";

export const mediaQueryInjectionKey: InjectionKey<MediaQueryProvider> =
    Symbol('mediaQueryInjectionKey')

export const layoutInjectionKey: InjectionKey<LayoutProvider> = Symbol('layoutInjectionKey')