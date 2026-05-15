import type {ButtonAnimationProps} from "/@/components/button-animation/types.ts";

export const buttonAnimationInjectionKey: InjectionKey<Partial<ButtonAnimationProps>> = Symbol(
    'buttonAnimationInjectionKey',
)
